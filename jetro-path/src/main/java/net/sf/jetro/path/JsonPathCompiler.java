/*
 * #%L
 * Jetro JsonPath
 * %%
 * Copyright (C) 2013 - 2016 The original author or authors.
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package net.sf.jetro.path;

import java.util.Arrays;

/**
 * This class compiles JsonPath strings to JsonPath objects.
 * 
 * @author Matthias Rothe
 * @see JsonPath#compile(String)
 */
public class JsonPathCompiler {
	private enum JsonPathToken {
		START_PATH("$"), START_NAME("."), NAME(null), START_INDEX("["), INDEX(null),
		END_INDEX("]"), CHARACTER(null), WILDCARD(JsonPath.WILDCARD),
		MATCHES_ALL_FURTHER(":"), END_PATH(""), OPTIONAL(JsonPath.OPTIONAL),
		END_OF_ARRAY(JsonPath.END_OF_ARRAY);

		private String sequence;

		JsonPathToken(final String sequence) {
			this.sequence = sequence;
		}

		String getSequence() {
			return sequence;
		}

		static JsonPathToken forChar(final char character) {
			for (JsonPathToken token : values()) {
				if (token.sequence != null && !"".equals(token.sequence)
						&& token.sequence.charAt(0) == character) {
					return token;
				}
			}

			return CHARACTER;
		}
	}

	private class CompilerContext {
		private JsonPathToken[] startTokens;
		private char[] jsonPath;
		private int currPos;
		private int offset;

		private JsonPathBuilder builder = new JsonPathBuilder();
		private JsonPathToken currentToken;
	}

	private ThreadLocal<CompilerContext> contexts = new ThreadLocal<CompilerContext>() {
		@Override
		protected CompilerContext initialValue() {
			return new CompilerContext();
		}
	};

	private static final JsonPathToken[] START_TOKENS = new JsonPathToken[] { 
			JsonPathToken.START_NAME, JsonPathToken.START_INDEX,
			JsonPathToken.MATCHES_ALL_FURTHER };

	/**
	 * Compiles the given JsonPath string to a JsonPath object.
	 * 
	 * @param jsonPath the JsonPath string to compile
	 * @return the compiled JsonPath object
	 * @throws JsonPathCompilerException if the given jsonPath cannot be compiled
	 * @throws IllegalArgumentException if the given jsonPath is null or empty
	 * @see JsonPath#compile(String)
	 */
	public JsonPath compile(final String jsonPath) {
		if (jsonPath == null || "".equals(jsonPath)) {
			throw new IllegalArgumentException("jsonPath must not be null or empty");
		}

		if ("$".equals(jsonPath)) {
			return new JsonPath();
		}

		CompilerContext context = contexts.get();
		context.startTokens = START_TOKENS;
		context.jsonPath = jsonPath.toCharArray();

		try {
			return doCompile(context);
		} finally {
			reset(context);
		}
	}

	private JsonPath doCompile(final CompilerContext context) {
		consume(context, expect(context, JsonPathToken.START_PATH));

		if (context.currPos == context.jsonPath.length) {
			throwExpectedAnyTokens(context, context.startTokens, JsonPathToken.END_PATH, context.currPos + 1);
		}

		while (context.currPos < context.jsonPath.length) {
			if (context.builder.getDepth() == 1) {
				// Append OPTIONAL to the start tokens for correct error messages
				JsonPathToken[] newStartTokens = new JsonPathToken[context.startTokens.length + 1];
				System.arraycopy(context.startTokens, 0, newStartTokens, 0, context.startTokens.length);
				newStartTokens[newStartTokens.length - 1] = JsonPathToken.OPTIONAL;
				context.startTokens = newStartTokens;
			}

			JsonPathToken token = expect(context, context.startTokens);
			consume(context, token);

			switch (token) {
			case START_NAME:
				consume(context, expect(context, JsonPathToken.NAME));
				break;
			case START_INDEX:
				consume(context, expect(context, JsonPathToken.INDEX));
				break;
			default:
				break;
			}
		}

		return context.builder.build();
	}

	private JsonPathToken expect(final CompilerContext context, final JsonPathToken... expectedTokens) {
		if (context.currPos == context.jsonPath.length) {
			if (expectedTokens.length == 1 && expectedTokens[0] == JsonPathToken.OPTIONAL) {
				// Return null if at end of json path and expected token is OPTIONAL
				return null;
			}

			context.currPos++;

			if (expectedTokens.length == 1) {
				throwExpectedToken(context, expectedTokens[0], JsonPathToken.END_PATH, context.currPos);
			} else {
				throwExpectedAnyTokens(context, expectedTokens, JsonPathToken.END_PATH, context.currPos);
			}
		}

		JsonPathToken currentToken = JsonPathToken.forChar(
				context.jsonPath[context.currPos]);
		boolean found = false;

		for (JsonPathToken expectedToken : expectedTokens) {
			if (isExpectedTokenOrValidSubstitute(currentToken, expectedToken)) {
				context.currentToken = currentToken;
				currentToken = expectedToken;
				found = true;
				break;
			}
		}

		if (found) {
			return currentToken;
		} else if (expectedTokens.length == 1 && expectedTokens[0] == JsonPathToken.OPTIONAL) {
			return null;
		} else {
			context.currPos++;

			if (expectedTokens.length == 1) {
				throwExpectedToken(context, expectedTokens[0], currentToken, context.currPos);
			} else {
				throwExpectedAnyTokens(context, expectedTokens, currentToken, context.currPos);
			}
		}

		// just to satisfy compiler
		return null;
	}

	private boolean isExpectedTokenOrValidSubstitute(JsonPathToken currentToken, JsonPathToken expectedToken) {
		return ((expectedToken == JsonPathToken.NAME || 
				expectedToken == JsonPathToken.INDEX) &&
				(currentToken == JsonPathToken.CHARACTER ||
				currentToken == JsonPathToken.WILDCARD ||
				currentToken == JsonPathToken.END_OF_ARRAY))
				|| currentToken == expectedToken;
	}

	private void consume(final CompilerContext context, final JsonPathToken token) {
		switch (token) {
		case NAME:
			consumeName(context);
			break;
		case INDEX:
			consumeIndex(context);
			break;
		case MATCHES_ALL_FURTHER:
			consumeMatchesAllFurther(context);
		default:
			context.currPos += token.getSequence().length();
		}
	}

	private void consumeName(final CompilerContext context) {
		boolean wildcard = false;
		boolean optional = false;
		String name = null;

		// WILDCARD
		if (context.currentToken == JsonPathToken.WILDCARD) {
			context.currPos += context.currentToken.getSequence().length();
			wildcard = true;

		// PROPERTY NAME
		} else {
			context.offset = context.currPos;

			proceed: for (context.currPos++; context.currPos < context.jsonPath.length; context.currPos++) {
				JsonPathToken currentToken = JsonPathToken.forChar(context.jsonPath[context.currPos]);

				switch (currentToken) {
				case END_OF_ARRAY:
				case CHARACTER:
					break;
				case OPTIONAL:
				case START_NAME:
				case START_INDEX:
				case MATCHES_ALL_FURTHER:
					break proceed;
				default:
					throwExpectedAnyTokens(context, new JsonPathToken[] {
							JsonPathToken.CHARACTER, JsonPathToken.START_NAME,
							JsonPathToken.START_INDEX, JsonPathToken.MATCHES_ALL_FURTHER,
							JsonPathToken.OPTIONAL },
						currentToken, context.currPos + 1);
				}
			}

			//CHECKSTYLE:OFF
			name = new String(context.jsonPath, context.offset, context.currPos - context.offset);
			//CHECKSTYLE:ON
		}

		// OPTIONAL
		JsonPathToken currentToken = expect(context, JsonPathToken.OPTIONAL);

		if (currentToken != null) {
			consume(context, currentToken);
			optional = true;
		}

		// Create PathElement
		if (wildcard) {
			context.builder.append(new PropertyNamePathElement(true, optional));
		} else {
			context.builder.append(new PropertyNamePathElement(name, optional));
		}
	}

	private void consumeIndex(final CompilerContext context) {
		boolean wildcard = false;
		boolean optional = false;
		boolean nextToLast = false;
		Integer index = -1;

		// WILDCARD
		if (context.currentToken == JsonPathToken.WILDCARD) {
			context.currPos += context.currentToken.getSequence().length();
			wildcard = true;

		// NEXT TO LAST
		} else if (context.currentToken == JsonPathToken.END_OF_ARRAY) {
			context.currPos += context.currentToken.getSequence().length();
			nextToLast = true;
			
		// INDEX
		} else {
			context.offset = context.currPos;

			proceed: for (context.currPos++; context.currPos < context.jsonPath.length; context.currPos++) {
				JsonPathToken currentToken = JsonPathToken.forChar(context.jsonPath[context.currPos]);

				switch (currentToken) {
				case CHARACTER:
					break;
				case END_INDEX:
					break proceed;
				default:
					throwExpectedToken(context, JsonPathToken.END_INDEX, currentToken, context.currPos + 1);
				}
			}

			if (context.currPos == context.jsonPath.length) {
				throwExpectedToken(context, JsonPathToken.END_INDEX, JsonPathToken.END_PATH, context.currPos + 1);
			}

			//CHECKSTYLE:OFF
			String chars = new String(context.jsonPath, context.offset, context.currPos - context.offset);
			//CHECKSTYLE:ON
			
			try {
				index = Integer.parseInt(chars);

				if (index < 0) {
					throw new NumberFormatException();
				}
			} catch (NumberFormatException e) {
				//CHECKSTYLE:OFF
				throw new JsonPathCompilerException("Expected a non-negative integer but found " + chars + " in "
						+ new String(context.jsonPath) + " at position " + (context.offset + 1));
				//CHECKSTYLE:ON
			}
		}

		// END INDEX
		consume(context, expect(context, JsonPathToken.END_INDEX));

		createArrayIndexElement(context, wildcard, optional, nextToLast, index);
	}

	private void createArrayIndexElement(final CompilerContext context, boolean wildcard, boolean optional,
			boolean nextToLast, Integer index) {
		if (nextToLast) {
			context.builder.append(new ArrayIndexPathElement());

			if (context.currPos < context.jsonPath.length) {
				JsonPathToken actualToken = JsonPathToken.forChar(
						context.jsonPath[context.currPos]);
				throwExpectedToken(context, JsonPathToken.END_PATH, actualToken,
						context.currPos + 1);
			}
		} else {
			// OPTIONAL
			JsonPathToken currentToken = expect(context, JsonPathToken.OPTIONAL);

			if (currentToken != null) {
				consume(context, currentToken);
				optional = true;
			}

			// Create PathElement
			if (wildcard) {
				context.builder.append(new ArrayIndexPathElement(true, optional));
			} else {
				context.builder.append(new ArrayIndexPathElement(index, optional));
			}			
		}
	}

	private void consumeMatchesAllFurther(final CompilerContext context) {
		context.builder.append(new MatchesAllFurtherPathElement());

		if (++context.currPos < context.jsonPath.length) {
			JsonPathToken actualToken = JsonPathToken.forChar(context.jsonPath[context.currPos]);
			throwExpectedToken(context, JsonPathToken.END_PATH, actualToken, context.currPos + 1);
		}
	}

	private void throwExpectedToken(final CompilerContext context, final JsonPathToken expectedToken,
			final JsonPathToken actualToken, final int position) {
		//CHECKSTYLE:OFF
		throw new JsonPathCompilerException("Expected " + expectedToken + " but found " + actualToken + " in "
				+ new String(context.jsonPath) + " at position " + position);
		//CHECKSTYLE:ON
	}

	private void throwExpectedAnyTokens(final CompilerContext context, final JsonPathToken[] expectedTokens,
			final JsonPathToken actualToken, final int position) {
		//CHECKSTYLE:OFF
		throw new JsonPathCompilerException("Expected any of " + Arrays.toString(expectedTokens) + " but found "
				+ actualToken + " in " + new String(context.jsonPath) + " at position " + position);
		//CHECKSTYLE:ON
	}

	private void reset(final CompilerContext context) {
		context.builder.reset();
		context.jsonPath = null;
		context.currentToken = null;
		context.currPos = 0;
		context.offset = 0;
	}
}
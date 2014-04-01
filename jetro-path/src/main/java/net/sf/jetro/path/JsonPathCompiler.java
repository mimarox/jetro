package net.sf.jetro.path;

import java.util.Arrays;

public class JsonPathCompiler {
	private enum JsonPathToken {
		START_PATH("$"), START_NAME("."), NAME(null), START_INDEX("["), INDEX(null), END_INDEX("]"), CHARACTER(null), WILDCARD(
				JsonPath.WILDCARD), MATCHES_ALL_FURTHER(":"), END_PATH(""), OPTIONAL(JsonPath.OPTIONAL);

		private String sequence;

		private JsonPathToken(final String sequence) {
			this.sequence = sequence;
		}

		String getSequence() {
			return sequence;
		}

		static JsonPathToken forChar(final char character) {
			for (JsonPathToken token : values()) {
				if (token.sequence != null && !"".equals(token.sequence) && token.sequence.charAt(0) == character) {
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

	private static final JsonPathToken[] START_TOKENS = new JsonPathToken[] { JsonPathToken.START_NAME,
			JsonPathToken.START_INDEX, JsonPathToken.MATCHES_ALL_FURTHER };

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

		JsonPathToken currentToken = JsonPathToken.forChar(context.jsonPath[context.currPos]);
		boolean found = false;

		for (JsonPathToken expectedToken : expectedTokens) {
			if (((expectedToken == JsonPathToken.NAME || expectedToken == JsonPathToken.INDEX) && (currentToken == JsonPathToken.CHARACTER || currentToken == JsonPathToken.WILDCARD))
					|| currentToken == expectedToken) {
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
				case CHARACTER:
					break;
				case OPTIONAL:
				case START_NAME:
				case START_INDEX:
				case MATCHES_ALL_FURTHER:
					break proceed;
				default:
					throwExpectedAnyTokens(context, new JsonPathToken[] { JsonPathToken.CHARACTER,
							JsonPathToken.START_NAME, JsonPathToken.START_INDEX, JsonPathToken.MATCHES_ALL_FURTHER,
							JsonPathToken.OPTIONAL },
						currentToken, context.currPos + 1);
				}
			}

			name = new String(context.jsonPath, context.offset, context.currPos - context.offset);
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
		Integer index = -1;

		// WILDCARD
		if (context.currentToken == JsonPathToken.WILDCARD) {
			context.currPos += context.currentToken.getSequence().length();
			wildcard = true;

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

			String chars = new String(context.jsonPath, context.offset, context.currPos - context.offset);

			try {
				index = Integer.parseInt(chars);

				if (index < 0) {
					throw new NumberFormatException();
				}
			} catch (NumberFormatException e) {
				throw new JsonPathCompilerException("Expected a non-negative integer but found " + chars + " in "
						+ new String(context.jsonPath) + " at position " + (context.offset + 1));
			}
		}

		// END INDEX
		consume(context, expect(context, JsonPathToken.END_INDEX));

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

	private void consumeMatchesAllFurther(final CompilerContext context) {
		context.builder.append(new MatchesAllFurtherPathElement());

		if (++context.currPos < context.jsonPath.length) {
			JsonPathToken actualToken = JsonPathToken.forChar(context.jsonPath[context.currPos]);
			throwExpectedToken(context, JsonPathToken.END_PATH, actualToken, context.currPos + 1);
		}
	}

	private void throwExpectedToken(final CompilerContext context, final JsonPathToken expectedToken,
			final JsonPathToken actualToken, final int position) {
		throw new JsonPathCompilerException("Expected " + expectedToken + " but found " + actualToken + " in "
				+ new String(context.jsonPath) + " at position " + position);
	}

	private void throwExpectedAnyTokens(final CompilerContext context, final JsonPathToken[] expectedTokens,
			final JsonPathToken actualToken, final int position) {
		throw new JsonPathCompilerException("Expected any of " + Arrays.toString(expectedTokens) + " but found "
				+ actualToken + " in " + new String(context.jsonPath) + " at position " + position);
	}

	private void reset(final CompilerContext context) {
		context.builder.reset();
		context.jsonPath = null;
		context.currentToken = null;
		context.currPos = 0;
		context.offset = 0;
	}
}
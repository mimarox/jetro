package net.sf.jetro.path;

import java.util.Arrays;

public class JsonPathCompiler {
	private enum JsonPathToken {
		START_PATH("$"), START_NAME("."), NAME(null), START_INDEX("["), INDEX(null), END_INDEX("]"), CHARACTER(null), WILDCARD(
				JsonPath.WILDCARD), MATCHES_ALL_FURTHER(":"), END_PATH("");

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

		CompilerContext context = contexts.get();
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
			throwExpectedAnyTokens(context, START_TOKENS, JsonPathToken.END_PATH, context.currPos + 1);
		}

		while (context.currPos < context.jsonPath.length) {
			JsonPathToken token = expect(context, START_TOKENS);
			consume(context, token);

			switch (token) {
			case START_NAME:
				consume(context, expect(context, JsonPathToken.NAME));
				break;
			case START_INDEX:
				consume(context, expect(context, JsonPathToken.INDEX));
				consume(context, expect(context, JsonPathToken.END_INDEX));
				break;
			}
		}

		return context.builder.build();
	}

	private JsonPathToken expect(final CompilerContext context, final JsonPathToken... expectedTokens) {
		if (context.currPos == context.jsonPath.length) {
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
		if (context.currentToken == JsonPathToken.WILDCARD) {
			context.currPos += context.currentToken.getSequence().length();
			context.builder.append(new PropertyNamePathElement(true));
		} else {
			context.offset = context.currPos;

			proceed: for (context.currPos++; context.currPos < context.jsonPath.length; context.currPos++) {
				JsonPathToken currentToken = JsonPathToken.forChar(context.jsonPath[context.currPos]);

				switch (currentToken) {
				case CHARACTER:
					break;
				case START_NAME:
				case START_INDEX:
				case MATCHES_ALL_FURTHER:
					break proceed;
				default:
					throwExpectedAnyTokens(context, new JsonPathToken[] { JsonPathToken.CHARACTER,
							JsonPathToken.START_NAME, JsonPathToken.START_INDEX, JsonPathToken.MATCHES_ALL_FURTHER },
						currentToken, context.currPos + 1);
				}
			}

			context.builder.append(new PropertyNamePathElement(new String(context.jsonPath, context.offset,
				context.currPos - context.offset)));
		}
	}

	private void consumeIndex(final CompilerContext context) {
		if (context.currentToken == JsonPathToken.WILDCARD) {
			context.currPos += context.currentToken.getSequence().length();
			context.builder.append(new ArrayIndexPathElement(true));
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
				Integer index = Integer.parseInt(chars);

				if (index < 0) {
					throw new NumberFormatException();
				}

				context.builder.append(new ArrayIndexPathElement(index));
			} catch (NumberFormatException e) {
				throw new JsonPathCompilerException("Expected a non-negative integer but found " + chars + " in "
						+ new String(context.jsonPath) + " at position " + (context.offset + 1));
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
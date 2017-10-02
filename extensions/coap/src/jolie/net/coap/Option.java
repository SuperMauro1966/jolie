package jolie.net.coap;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMap;

import java.util.HashMap;

public abstract class Option {

    public enum Occurence {
	NONE, ONCE, MULTIPLE
    }

    public static final int UNKNOWN = -1;
    public static final int IF_MATCH = 1;
    public static final int URI_HOST = 3;
    public static final int ETAG = 4;
    public static final int IF_NONE_MATCH = 5;
    public static final int OBSERVE = 6;
    public static final int URI_PORT = 7;
    public static final int LOCATION_PATH = 8;
    public static final int URI_PATH = 11;
    public static final int CONTENT_FORMAT = 12;
    public static final int MAX_AGE = 14;
    public static final int URI_QUERY = 15;
    public static final int ACCEPT = 17;
    public static final int LOCATION_QUERY = 20;
    public static final int BLOCK_2 = 23;
    public static final int BLOCK_1 = 27;
    public static final int SIZE_2 = 28;
    public static final int PROXY_URI = 35;
    public static final int PROXY_SCHEME = 39;
    public static final int SIZE_1 = 60;
    public static final int ENDPOINT_ID_1 = 124;
    public static final int ENDPOINT_ID_2 = 189;
    private static HashMap<Integer, String> OPTIONS = new HashMap<>();

    static {
	OPTIONS.putAll(ImmutableMap.<Integer, String>builder()
		.put(IF_MATCH, "IF MATCH (" + IF_MATCH + ")")
		.put(URI_HOST, "URI HOST (" + URI_HOST + ")")
		.put(ETAG, "ETAG (" + ETAG + ")")
		.put(IF_NONE_MATCH, "IF NONE MATCH (" + IF_NONE_MATCH + ")")
		.put(OBSERVE, "OBSERVE (" + OBSERVE + ")")
		.put(URI_PORT, "URI PORT (" + URI_PORT + ")")
		.put(LOCATION_PATH, "LOCATION PATH (" + LOCATION_PATH + ")")
		.put(URI_PATH, "URI PATH (" + URI_PATH + ")")
		.put(CONTENT_FORMAT, "CONTENT FORMAT (" + CONTENT_FORMAT + ")")
		.put(MAX_AGE, "MAX AGE (" + MAX_AGE + ")")
		.put(URI_QUERY, "URI QUERY (" + URI_QUERY + ")")
		.put(ACCEPT, "ACCEPT (" + ACCEPT + ")")
		.put(LOCATION_QUERY, "LOCATION QUERY (" + LOCATION_QUERY + ")")
		.put(BLOCK_2, "BLOCK 2 (" + BLOCK_2 + ")")
		.put(BLOCK_1, "BLOCK 1 (" + BLOCK_1 + ")")
		.put(SIZE_2, "SIZE 2 (" + SIZE_2 + ")")
		.put(PROXY_URI, "PROXY URI (" + PROXY_URI + ")")
		.put(PROXY_SCHEME, "PROXY SCHEME (" + PROXY_SCHEME + ")")
		.put(SIZE_1, "SIZE 1 (" + SIZE_1 + ")")
		.put(ENDPOINT_ID_1, "ENDPOINT ID 1 (" + ENDPOINT_ID_1 + ")")
		.put(ENDPOINT_ID_2, "ENDPOINT ID 2 (" + ENDPOINT_ID_2 + ")")
		.build()
	);
    }

    public static String asString(int optionNumber) {
	String result = OPTIONS.get(optionNumber);
	return result == null ? "UNKOWN (" + optionNumber + ")" : result;
    }

    private static HashMultimap<Integer, Integer> MUTUAL_EXCLUSIONS = HashMultimap.create();

    static {
	MUTUAL_EXCLUSIONS.put(URI_HOST, PROXY_URI);
	MUTUAL_EXCLUSIONS.put(PROXY_URI, URI_HOST);

	MUTUAL_EXCLUSIONS.put(URI_PORT, PROXY_URI);
	MUTUAL_EXCLUSIONS.put(PROXY_URI, URI_PORT);

	MUTUAL_EXCLUSIONS.put(URI_PATH, PROXY_URI);
	MUTUAL_EXCLUSIONS.put(PROXY_URI, URI_PATH);

	MUTUAL_EXCLUSIONS.put(URI_QUERY, PROXY_URI);
	MUTUAL_EXCLUSIONS.put(PROXY_URI, URI_QUERY);

	MUTUAL_EXCLUSIONS.put(PROXY_SCHEME, PROXY_URI);
	MUTUAL_EXCLUSIONS.put(PROXY_URI, PROXY_SCHEME);
    }

    public static boolean mutuallyExcludes(int firstOptionNumber, int secondOptionNumber) {
	return MUTUAL_EXCLUSIONS.get(firstOptionNumber).contains(secondOptionNumber);
    }

    private static final HashBasedTable<Integer, Integer, Option.Occurence> OCCURENCE_CONSTRAINTS
	    = HashBasedTable.create();

    static {
	// GET Requests
	OCCURENCE_CONSTRAINTS.row(MessageCode.GET).putAll(ImmutableMap.<Integer, Occurence>builder()
		.put(URI_HOST, Occurence.ONCE)
		.put(URI_PORT, Occurence.ONCE)
		.put(URI_PATH, Occurence.MULTIPLE)
		.put(URI_QUERY, Occurence.MULTIPLE)
		.put(PROXY_URI, Occurence.ONCE)
		.put(PROXY_SCHEME, Occurence.ONCE)
		.put(ACCEPT, Occurence.MULTIPLE)
		.put(ETAG, Occurence.MULTIPLE)
		.put(OBSERVE, Occurence.ONCE)
		.put(BLOCK_2, Occurence.ONCE)
		.put(SIZE_2, Occurence.ONCE)
		.put(ENDPOINT_ID_1, Occurence.ONCE)
		.build()
	);

	// POST Requests
	OCCURENCE_CONSTRAINTS.row(MessageCode.POST).putAll(ImmutableMap.<Integer, Occurence>builder()
		.put(URI_HOST, Occurence.ONCE)
		.put(URI_PORT, Occurence.ONCE)
		.put(URI_PATH, Occurence.MULTIPLE)
		.put(URI_QUERY, Occurence.MULTIPLE)
		.put(ACCEPT, Occurence.MULTIPLE)
		.put(PROXY_URI, Occurence.ONCE)
		.put(PROXY_SCHEME, Occurence.ONCE)
		.put(CONTENT_FORMAT, Occurence.ONCE)
		.put(BLOCK_2, Occurence.ONCE)
		.put(BLOCK_1, Occurence.ONCE)
		.put(SIZE_2, Occurence.ONCE)
		.put(SIZE_1, Occurence.ONCE)
		.put(ENDPOINT_ID_1, Occurence.ONCE)
		.build()
	);

	// PUT Requests
	OCCURENCE_CONSTRAINTS.row(MessageCode.PUT).putAll(ImmutableMap.<Integer, Occurence>builder()
		.put(URI_HOST, Occurence.ONCE)
		.put(URI_PORT, Occurence.ONCE)
		.put(URI_PATH, Occurence.MULTIPLE)
		.put(URI_QUERY, Occurence.MULTIPLE)
		.put(ACCEPT, Occurence.MULTIPLE)
		.put(PROXY_URI, Occurence.ONCE)
		.put(PROXY_SCHEME, Occurence.ONCE)
		.put(CONTENT_FORMAT, Occurence.ONCE)
		.put(IF_MATCH, Occurence.ONCE)
		.put(IF_NONE_MATCH, Occurence.ONCE)
		.put(BLOCK_2, Occurence.ONCE)
		.put(BLOCK_1, Occurence.ONCE)
		.put(SIZE_2, Occurence.ONCE)
		.put(SIZE_1, Occurence.ONCE)
		.put(ENDPOINT_ID_1, Occurence.ONCE)
		.build()
	);

	// DELETE Requests
	OCCURENCE_CONSTRAINTS.row(MessageCode.DELETE).putAll(ImmutableMap.<Integer, Occurence>builder()
		.put(URI_HOST, Occurence.ONCE)
		.put(URI_PORT, Occurence.ONCE)
		.put(URI_PATH, Occurence.MULTIPLE)
		.put(URI_QUERY, Occurence.MULTIPLE)
		.put(PROXY_URI, Occurence.ONCE)
		.put(PROXY_SCHEME, Occurence.ONCE)
		.put(ENDPOINT_ID_1, Occurence.ONCE)
		.build()
	);

	//Response success (2.x)
	OCCURENCE_CONSTRAINTS.row(MessageCode.CREATED_201).putAll(ImmutableMap.<Integer, Occurence>builder()
		.put(ETAG, Occurence.ONCE)
		.put(OBSERVE, Occurence.ONCE)
		.put(LOCATION_PATH, Occurence.MULTIPLE)
		.put(LOCATION_QUERY, Occurence.MULTIPLE)
		.put(CONTENT_FORMAT, Occurence.ONCE)
		.put(BLOCK_2, Occurence.ONCE)
		.put(BLOCK_1, Occurence.ONCE)
		.put(SIZE_2, Occurence.ONCE)
		.put(ENDPOINT_ID_2, Occurence.ONCE)
		.build()
	);

	OCCURENCE_CONSTRAINTS.row(MessageCode.DELETED_202).putAll(ImmutableMap.<Integer, Occurence>builder()
		.put(CONTENT_FORMAT, Occurence.ONCE)
		.put(BLOCK_2, Occurence.ONCE)
		.put(BLOCK_1, Occurence.ONCE)
		.put(SIZE_2, Occurence.ONCE)
		.put(ENDPOINT_ID_2, Occurence.ONCE)
		.build()
	);

	OCCURENCE_CONSTRAINTS.row(MessageCode.VALID_203).putAll(ImmutableMap.<Integer, Occurence>builder()
		.put(OBSERVE, Occurence.ONCE)
		.put(ETAG, Occurence.ONCE)
		.put(MAX_AGE, Occurence.ONCE)
		.put(CONTENT_FORMAT, Occurence.ONCE)
		.put(ENDPOINT_ID_1, Occurence.ONCE)
		.put(ENDPOINT_ID_2, Occurence.ONCE)
		.build()
	);

	OCCURENCE_CONSTRAINTS.row(MessageCode.CHANGED_204).putAll(ImmutableMap.<Integer, Occurence>builder()
		.put(ETAG, Occurence.ONCE)
		.put(CONTENT_FORMAT, Occurence.ONCE)
		.put(BLOCK_2, Occurence.ONCE)
		.put(BLOCK_1, Occurence.ONCE)
		.put(SIZE_2, Occurence.ONCE)
		.put(ENDPOINT_ID_2, Occurence.ONCE)
		.build()
	);

	OCCURENCE_CONSTRAINTS.row(MessageCode.CONTENT_205).putAll(ImmutableMap.<Integer, Occurence>builder()
		.put(OBSERVE, Occurence.ONCE)
		.put(CONTENT_FORMAT, Occurence.ONCE)
		.put(MAX_AGE, Occurence.ONCE)
		.put(ETAG, Occurence.ONCE)
		.put(BLOCK_2, Occurence.ONCE)
		.put(BLOCK_1, Occurence.ONCE)
		.put(SIZE_2, Occurence.ONCE)
		.put(ENDPOINT_ID_1, Occurence.ONCE)
		.put(ENDPOINT_ID_2, Occurence.ONCE)
		.build()
	);

	OCCURENCE_CONSTRAINTS.row(MessageCode.CONTINUE_231).putAll(ImmutableMap.<Integer, Occurence>builder()
		.put(BLOCK_1, Occurence.ONCE)
		.build()
	);

	// Client ERROR Responses (4.x)
	OCCURENCE_CONSTRAINTS.row(MessageCode.BAD_REQUEST_400).putAll(ImmutableMap.<Integer, Occurence>builder()
		.put(MAX_AGE, Occurence.ONCE)
		.put(CONTENT_FORMAT, Occurence.ONCE)
		.put(ENDPOINT_ID_2, Occurence.ONCE)
		.build()
	);

	OCCURENCE_CONSTRAINTS.row(MessageCode.UNAUTHORIZED_401).putAll(ImmutableMap.<Integer, Occurence>builder()
		.put(MAX_AGE, Occurence.ONCE)
		.put(CONTENT_FORMAT, Occurence.ONCE)
		.put(ENDPOINT_ID_2, Occurence.ONCE)
		.build()
	);

	OCCURENCE_CONSTRAINTS.row(MessageCode.BAD_OPTION_402).putAll(ImmutableMap.<Integer, Occurence>builder()
		.put(MAX_AGE, Occurence.ONCE)
		.put(CONTENT_FORMAT, Occurence.ONCE)
		.put(ENDPOINT_ID_2, Occurence.ONCE)
		.build()
	);

	OCCURENCE_CONSTRAINTS.row(MessageCode.FORBIDDEN_403).putAll(ImmutableMap.<Integer, Occurence>builder()
		.put(MAX_AGE, Occurence.ONCE)
		.put(CONTENT_FORMAT, Occurence.ONCE)
		.put(ENDPOINT_ID_2, Occurence.ONCE)
		.build()
	);

	OCCURENCE_CONSTRAINTS.row(MessageCode.NOT_FOUND_404).putAll(ImmutableMap.<Integer, Occurence>builder()
		.put(MAX_AGE, Occurence.ONCE)
		.put(CONTENT_FORMAT, Occurence.ONCE)
		.put(ENDPOINT_ID_2, Occurence.ONCE)
		.build()
	);

	OCCURENCE_CONSTRAINTS.row(MessageCode.METHOD_NOT_ALLOWED_405).putAll(ImmutableMap.<Integer, Occurence>builder()
		.put(MAX_AGE, Occurence.ONCE)
		.put(CONTENT_FORMAT, Occurence.ONCE)
		.put(ENDPOINT_ID_2, Occurence.ONCE)
		.build()
	);

	OCCURENCE_CONSTRAINTS.row(MessageCode.NOT_ACCEPTABLE_406).putAll(ImmutableMap.<Integer, Occurence>builder()
		.put(MAX_AGE, Occurence.ONCE)
		.put(CONTENT_FORMAT, Occurence.ONCE)
		.put(ENDPOINT_ID_2, Occurence.ONCE)
		.build()
	);

	OCCURENCE_CONSTRAINTS.row(MessageCode.REQUEST_ENTITY_INCOMPLETE_408).putAll(ImmutableMap.<Integer, Occurence>builder()
		.put(CONTENT_FORMAT, Occurence.ONCE)
		.build()
	);

	OCCURENCE_CONSTRAINTS.row(MessageCode.PRECONDITION_FAILED_412).putAll(ImmutableMap.<Integer, Occurence>builder()
		.put(MAX_AGE, Occurence.ONCE)
		.put(CONTENT_FORMAT, Occurence.ONCE)
		.put(ENDPOINT_ID_2, Occurence.ONCE)
		.build()
	);

	OCCURENCE_CONSTRAINTS.row(MessageCode.REQUEST_ENTITY_TOO_LARGE_413).putAll(ImmutableMap.<Integer, Occurence>builder()
		.put(MAX_AGE, Occurence.ONCE)
		.put(CONTENT_FORMAT, Occurence.ONCE)
		.put(BLOCK_1, Occurence.ONCE)
		.put(SIZE_1, Occurence.ONCE)
		.put(ENDPOINT_ID_2, Occurence.ONCE)
		.build()
	);

	OCCURENCE_CONSTRAINTS.row(MessageCode.UNSUPPORTED_CONTENT_FORMAT_415).putAll(ImmutableMap.<Integer, Occurence>builder()
		.put(MAX_AGE, Occurence.ONCE)
		.put(CONTENT_FORMAT, Occurence.ONCE)
		.put(ENDPOINT_ID_2, Occurence.ONCE)
		.build()
	);

	// Server ERROR Responses ( 5.x )
	OCCURENCE_CONSTRAINTS.row(MessageCode.INTERNAL_SERVER_ERROR_500).putAll(ImmutableMap.<Integer, Occurence>builder()
		.put(MAX_AGE, Occurence.ONCE)
		.put(CONTENT_FORMAT, Occurence.ONCE)
		.put(ENDPOINT_ID_2, Occurence.ONCE)
		.build()
	);

	OCCURENCE_CONSTRAINTS.row(MessageCode.NOT_IMPLEMENTED_501).putAll(ImmutableMap.<Integer, Occurence>builder()
		.put(MAX_AGE, Occurence.ONCE)
		.put(CONTENT_FORMAT, Occurence.ONCE)
		.put(ENDPOINT_ID_2, Occurence.ONCE)
		.build()
	);

	OCCURENCE_CONSTRAINTS.row(MessageCode.BAD_GATEWAY_502).putAll(ImmutableMap.<Integer, Occurence>builder()
		.put(MAX_AGE, Occurence.ONCE)
		.put(CONTENT_FORMAT, Occurence.ONCE)
		.put(ENDPOINT_ID_2, Occurence.ONCE)
		.build()
	);

	OCCURENCE_CONSTRAINTS.row(MessageCode.GATEWAY_TIMEOUT_504).putAll(ImmutableMap.<Integer, Occurence>builder()
		.put(MAX_AGE, Occurence.ONCE)
		.put(CONTENT_FORMAT, Occurence.ONCE)
		.put(ENDPOINT_ID_2, Occurence.ONCE)
		.build()
	);

	OCCURENCE_CONSTRAINTS.row(MessageCode.PROXYING_NOT_SUPPORTED_505).putAll(ImmutableMap.<Integer, Occurence>builder()
		.put(MAX_AGE, Occurence.ONCE)
		.put(CONTENT_FORMAT, Occurence.ONCE)
		.put(ENDPOINT_ID_2, Occurence.ONCE)
		.build()
	);
    }

    public static Occurence getPermittedOccurrence(int optionNumber, int messageCode) {
	Occurence result = OCCURENCE_CONSTRAINTS.get(messageCode, optionNumber);
	return result == null ? Occurence.NONE : result;
    }

    public static boolean isCritical(int optionNumber) {
	return (optionNumber & 1) == 1;
    }

    public static boolean isSafe(int optionNumber) {
	return !((optionNumber & 2) == 2);
    }

    public static boolean isCacheKey(int optionNumber) {
	return !((optionNumber & 0x1e) == 0x1c);
    }
}

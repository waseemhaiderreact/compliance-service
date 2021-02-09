package com.alsharqi.compliance.security;

public class Authorities {public static final String ROLE_ADMIN = "ROLE_ADMIN";
    private static final String PRIVILEGE_PREFIX = "PRIVILEGE_";

    private static final String COMPLIANCE = "COMPLIANCE";
    private static final String SQUAD = "SQUAD";

    public static final String _CREATE = "_CREATE";
    public static final String _EDIT = "_EDIT";
    public static final String _READ = "_READ";
    public static final String _FULL = "_FULL";

    public static final class Compliance {
        public static final String FULL = PRIVILEGE_PREFIX+ COMPLIANCE +_FULL;
        public static final String CREATE = PRIVILEGE_PREFIX+ COMPLIANCE +_CREATE;
        public static final String READ = PRIVILEGE_PREFIX + COMPLIANCE + _READ;
        public static final String EDIT = PRIVILEGE_PREFIX + COMPLIANCE + _EDIT;
    }

    public static final class Squad {
        public static final String FULL = PRIVILEGE_PREFIX+SQUAD+_FULL;
        public static final String CREATE = PRIVILEGE_PREFIX+SQUAD+_CREATE;
        public static final String READ = PRIVILEGE_PREFIX + SQUAD + _READ;
        public static final String EDIT = PRIVILEGE_PREFIX + SQUAD + _EDIT;
    }
}

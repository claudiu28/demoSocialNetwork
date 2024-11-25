package ubb.scs.map.demosocialnetwork.utils.extract;

public class Extract {
    public static String extractUsername(String input) {
        if (input == null || input.isEmpty()) {
            return "";
        }

        int usernameStart = input.indexOf("Username:") + 9;
        int usernameEnd = input.indexOf("->");

        if (usernameStart >= 9 && usernameEnd > usernameStart) {
            return input.substring(usernameStart, usernameEnd).trim();
        }

        return "";
    }

}

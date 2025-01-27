public class User {
    public static enum UserType {
        MANAGER(1), EMPLOYEE(2), TRAINEE(3);
        public int value;
        private UserType(int value) {
            this.value = value;
        }
    }
    private final int userId;
    private String userName;
    private UserType userType;

    //Constructor
    User(int userId, String userName, UserType userType) {
        this.userId = userId;
        this.userName = userName;
        this.userType = userType;
    }
}

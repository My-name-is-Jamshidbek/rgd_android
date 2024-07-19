package com.example.waterfilter.Login;

public class LoginResponse {
    private String message;
    private Data data;

    public String getMessage() {
        return message;
    }

    public Data getData() {
        return data;
    }

    public class Data {
        private User user;
        private String token;

        public User getUser() {
            return user;
        }

        public String getToken() {
            return token;
        }

        public class User {
            private int id;
            private String name;
            private int phone;
            private String created_at;
            private String updated_at;

            public int getId() {
                return id;
            }

            public String getName() {
                return name;
            }

            public int getPhone() {
                return phone;
            }

            public String getCreatedAt() {
                return created_at;
            }

            public String getUpdatedAt() {
                return updated_at;
            }
        }
    }
}

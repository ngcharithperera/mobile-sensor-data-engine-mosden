package au.csiro.gsnlite.storage;

public class DBConnectionInfo {
        private String driverClass, url, userName, password;

        public DBConnectionInfo(String driverClass, String url, String userName, String password) {
            this.driverClass = driverClass;
            this.url = url;
            this.userName = userName;
            this.password = password;
        }

        public boolean equals(Object o) {
            if (null == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            DBConnectionInfo that = (DBConnectionInfo) o;

            if (driverClass != null ? !driverClass.equals(that.driverClass) : that.driverClass != null) return false;
            if (password != null ? !password.equals(that.password) : that.password != null) return false;
            if (url != null ? !url.equals(that.url) : that.url != null) return false;
            if (userName != null ? !userName.equals(that.userName) : that.userName != null) return false;

            return true;
        }

        public String getDriverClass() {
            return driverClass;
        }

        public String getUrl() {
            return url;
        }

        public String getUserName() {
            return userName;
        }

        public String getPassword() {
            return password;
        }

        public int hashCode() {
            int result = driverClass != null ? driverClass.hashCode() : 0;
            result = 31 * result + (url != null ? url.hashCode() : 0);
            result = 31 * result + (userName != null ? userName.hashCode() : 0);
            result = 31 * result + (password != null ? password.hashCode() : 0);
            return Math.abs(result);
        }

    @Override
    public String toString() {
        return "DBConnectionInfo{" +
                "driverClass='" + driverClass + '\'' +
                ", url='" + url + '\'' +
                ", userName='" + userName + '\'' +
                '}';
    }
}
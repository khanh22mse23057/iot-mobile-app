package code.model;

import java.util.ArrayList;

public class JsonDataFeedModel {
    public class Data {
        public First first;
        public Last last;
        public int count;
    }

    public class Details {
        public Object shared_with;
        public Data data;
    }

    public class First {
        public String id;
        public String value;
        public int feed_id;
        public int group_id;
        public String expiration;
        public int lat;
        public int lon;
        public int ele;
        public String completed_at;
        public String created_at;
        public String updated_at;
        public int created_epoch;
    }

    public class Group {
        public int id;
        public String name;
        public String description;
        public String created_at;
        public String updated_at;
    }

    public class Group2 {
        public int id;
        public String name;
        public String description;
        public String created_at;
        public String updated_at;
    }

    public class Last {
        public String id;
        public String value;
        public int feed_id;
        public int group_id;
        public String expiration;
        public int lat;
        public int lon;
        public int ele;
        public String completed_at;
        public String created_at;
        public String updated_at;
        public int created_epoch;
    }

    public class Root {
        public int id;
        public String name;
        public String key;
        public Group group;
        public ArrayList<Group> groups;
        public String description;
        public Details details;
        public String unit_type;
        public String unit_symbol;
        public boolean history;
        public String visibility;
        public String license;
        public boolean enabled;
        public String last_value;
        public String status;
        public boolean status_notify;
        public int status_timeout;
        public String created_at;
        public String updated_at;
    }


}

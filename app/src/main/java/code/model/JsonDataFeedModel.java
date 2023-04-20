package code.model;

import java.util.ArrayList;

public class JsonDataFeedModel {
    public class Group{
        public int id;
        public String key;
        public String name;
        public int user_id;
    }

    public class Group2{
        public int id;
        public String key;
        public String name;
        public int user_id;
    }

    public class Owner{
        public int id;
        public String username;
    }

    public class Root{
        public String username;
        public Owner owner;
        public int id;
        public String name;
        public String description;
        public Object license;
        public boolean history;
        public boolean enabled;
        public String visibility;
        public Object unit_type;
        public Object unit_symbol;
        public String last_value;
        public String created_at;
        public String updated_at;
        public Object wipper_pin_info;
        public String key;
        public boolean writable;
        public Group group;
        public ArrayList<Group> groups;
    }




}

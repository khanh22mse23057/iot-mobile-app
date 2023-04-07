package code.connection;

import android.text.TextUtils;

import java.util.HashMap;
import java.util.Map;

public class ParamList {

    public String page;
    public String count;
    public String category;
    public String keyword;

    public ParamList() {
    }

    public Map<String, String> getParams() {
        Map<String, String> param = new HashMap<>();
        if (!TextUtils.isEmpty(page)) param.put("page", page);
        if (!TextUtils.isEmpty(count)) param.put("count", count);
        if (!TextUtils.isEmpty(category)) param.put("category", category);
        if (!TextUtils.isEmpty(keyword)) param.put("keyword", keyword);
        return param;
    }
}

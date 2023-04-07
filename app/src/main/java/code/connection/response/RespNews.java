package code.connection.response;

import java.io.Serializable;
import java.util.List;

import code.model.News;

public class RespNews implements Serializable {
    public int status = -1;
    public String messages = "";
    public int count = -1;
    public int count_total = -1;
    public int page = -1;
    public List<News> list;
}

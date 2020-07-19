package android.arnab.organisationalstudent;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class RequestPostBook extends StringRequest
{
    private static String REGISTER_REQUEST_URL= "http://arnabbanerjee.dx.am/addBookRequisition.php";
    private Map<String, String> params;
    int MY_SOCKET_TIMEOUT_MS=60000;

    public RequestPostBook(long id, ArrayList<String> bookNames, ArrayList<String> bookAuthors, ArrayList<String> subjects,
                           String date,Response.Listener<String> listener)
    {
        super(Method.POST, REGISTER_REQUEST_URL, listener, null);

        JSONArray bookNamesJSON = new JSONArray(Arrays.asList(bookNames));
        JSONArray bookAuthorsJSON = new JSONArray(Arrays.asList(bookAuthors));
        JSONArray subjectsJSON = new JSONArray(Arrays.asList(subjects));


        params = new HashMap<>();
        params.put("candidateId", id + "");
        params.put("bookName",bookNamesJSON.toString());
        params.put("authorName",bookAuthorsJSON.toString());
        params.put("subjectName",subjectsJSON.toString());
        params.put("reqDate",date);

        this.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }
}

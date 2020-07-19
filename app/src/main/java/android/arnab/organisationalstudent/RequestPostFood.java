package android.arnab.organisationalstudent;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class RequestPostFood extends StringRequest
{
    private static String REGISTER_REQUEST_URL= "http://arnabbanerjee.dx.am/RequestPostFood.php";
    private Map<String, String> params;
    int MY_SOCKET_TIMEOUT_MS=60000;

    public RequestPostFood(long id, ArrayList<String> itemName, ArrayList<Integer> quantity, ArrayList<Integer> price,
                           Response.Listener<String> listener)
    {
        super(Request.Method.POST, REGISTER_REQUEST_URL, listener, null);

        JSONArray itemNamesJSON = new JSONArray(Arrays.asList(itemName));
        JSONArray quantityJSON = new JSONArray(Arrays.asList(quantity));
        JSONArray priceJSON = new JSONArray(Arrays.asList(price));


        params = new HashMap<>();
        params.put("candidateId", id + "");
        params.put("itemName",itemNamesJSON.toString());
        params.put("quantity",quantityJSON.toString());
        params.put("price",priceJSON.toString());

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

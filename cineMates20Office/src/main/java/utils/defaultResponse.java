package utils;

import com.google.gson.annotations.SerializedName;

public class defaultResponse {
    public String getResponse() {
        return response;
    }

    @SerializedName("response")
    String response;

}

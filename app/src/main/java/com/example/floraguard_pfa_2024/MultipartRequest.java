package com.example.floraguard_pfa_2024;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MultipartRequest extends Request<String> {

    private final Response.Listener<String> mListener;
    private final Response.ErrorListener mErrorListener;
    private final Map<String, String> mParams;
    private final Map<String, DataPart> mByteData;
    private static final String BOUNDARY = "----BoundaryStringHere----";

    public MultipartRequest(String url, Map<String, String> params, Map<String, DataPart> byteData,
                            Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(Method.POST, url, errorListener);
        mListener = listener;
        mErrorListener = errorListener;
        mParams = params;
        mByteData = byteData;
    }

    @Override
    public Map<String, String> getHeaders() {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "multipart/form-data;boundary=" + BOUNDARY);
        return headers;
    }

    @Override
    protected Map<String, String> getParams() {
        return mParams;
    }

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        return Response.success(new String(response.data), getCacheEntry());
    }

    @Override
    protected void deliverResponse(String response) {
        mListener.onResponse(response);
    }

    @Override
    public void deliverError(VolleyError error) {
        mErrorListener.onErrorResponse(error);
    }

    @Override
    public byte[] getBody() {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(bos);

        try {
            // Write string params
            if (mParams != null && !mParams.isEmpty()) {
                for (Map.Entry<String, String> entry : mParams.entrySet()) {
                    buildTextPart(dos, entry.getKey(), entry.getValue());
                }
            }

            // Write byte data (binary)
            if (mByteData != null && !mByteData.isEmpty()) {
                for (Map.Entry<String, DataPart> entry : mByteData.entrySet()) {
                    buildDataPart(dos, entry.getKey(), entry.getValue());
                }
            }

            // Close the output stream
            dos.writeBytes("--" + BOUNDARY + "--\r\n");
            dos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bos.toByteArray();
    }

    private void buildTextPart(DataOutputStream dataOutputStream, String key, String value) throws IOException {
        dataOutputStream.writeBytes("--" + BOUNDARY + "\r\n");
        dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"" + key + "\"\r\n\r\n" + value + "\r\n");
    }

    private void buildDataPart(DataOutputStream dataOutputStream, String key, DataPart dataPart) throws IOException {
        dataOutputStream.writeBytes("--" + BOUNDARY + "\r\n");
        dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"" +
                key + "\"; filename=\"" + dataPart.getFileName() + "\"\r\n");
        dataOutputStream.writeBytes("Content-Type: " + dataPart.getMimeType() + "\r\n\r\n");
        dataOutputStream.write(dataPart.getData());
        dataOutputStream.writeBytes("\r\n");
    }

    public static class DataPart {
        private final String fileName;
        private final byte[] data;
        private final String mimeType;

        public DataPart(String fileName, byte[] data, String mimeType) {
            this.fileName = fileName;
            this.data = data;
            this.mimeType = mimeType;
        }

        public String getFileName() {
            return fileName;
        }

        public byte[] getData() {
            return data;
        }

        public String getMimeType() {
            return mimeType;
        }
    }
}

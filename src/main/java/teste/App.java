package teste;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class App {

    private static final String URL_SERVICO_UPLOAD = "url";

    private static final String BASIC_AUTHENTICATION = "auth.basic";

    private static final String X_TOKEN_ANEXOS = "auth.xtokensanexos";

    private static final String CAMINHO_ARQUIVO = "bytes.file";
   
    private static final String STRING_BYTES = "bytes.string";

    private Properties props;

	public static void main(String[] args) throws Exception {
        new App().start();
    }

    private void start() throws IOException {
        props = new Properties();
        FileInputStream file = new FileInputStream("./external-files/application.properties");
        props.load(file);
        proceedUsingFile();
        proceedUsingArrayBytes();
	}

    private void proceedUsingFile() throws IOException {
        File file = new File(getProperties(CAMINHO_ARQUIVO));
        InputStream inputStream = new FileInputStream(file);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int read = 0;
        while ((read = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, read);
        }
        inputStream.close();
        outputStream.close();

        sysOut("Upload de Anexos por meio de arquivo", request(outputStream));
	}

    private void proceedUsingArrayBytes() throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write(STRING_BYTES.getBytes());
        outputStream.close();

        sysOut("Upload de Anexos por meio de array de bytes", request(outputStream));
    }

    private Response request(ByteArrayOutputStream outputStream) throws IOException {
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("uploadFile", null,
                        RequestBody.create(MediaType.parse("application/octet-stream"), outputStream.toByteArray()))
                .build();
        Request request = new Request.Builder().url(getProperties(URL_SERVICO_UPLOAD)).method("POST", body)
                .addHeader("X-Token-Anexos", getProperties(X_TOKEN_ANEXOS))
                .addHeader("Authorization", getProperties(BASIC_AUTHENTICATION)).build();
        return client.newCall(request).execute();
    }

    private void sysOut(String texto, Response response) throws IOException {
        System.out.println("");
        System.out.println(texto);
        System.out.println(response.toString());
        System.out.println(response.body().string());
    }

    private String getProperties(String chave) throws IOException {
        return props.getProperty(chave);
    }

}

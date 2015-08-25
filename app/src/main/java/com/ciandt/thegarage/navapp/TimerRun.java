package com.ciandt.thegarage.navapp;

import android.content.Context;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.ciandt.thegarage.navapp.Repository;
import com.ciandt.thegarage.navapp.VolleyApplication;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Emerson on 18/08/2015.
 */
public class TimerRun {

    private TimerTask task;
    private Timer timer;
    private Context context;
    private static final String UrlService = "http://citbeacons.appspot.com/ws/beacon/findByMacAddress/macAddress=";
    private Repository repository;
    final ArrayList<String> beaconsAntecessoresLista= new ArrayList<>();
    final LinkedHashMap<String,String> mensagensBeaconsAntecessoresChaveValor = new LinkedHashMap<>();



    public TimerRun(Context context) {
        this.context = context;
        repository = new Repository(context);
        timer = new Timer();
    }

    public void start() {
        task = new TimerTask() {
            @Override
            public void run() {
                String beaconMacAddress = repository.get("macaddress");

                JsonObjectRequest request = new JsonObjectRequest(UrlService + beaconMacAddress, null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    JSONObject jsonResult = response.getJSONObject("payload");
                                    String beaconsAntecessores = jsonResult.getString("description");
                                    String mensagemBeacon = jsonResult.getString("message");

                                    String splitTemp[] = beaconsAntecessores.split("\\;");
                                    for (int i = 0; i < splitTemp.length; i++) {
                                        beaconsAntecessoresLista.add(splitTemp[i]);
                                    }

                                    splitTemp = mensagemBeacon.split("\\|");
                                    for (int i = 0; i < splitTemp.length; i++) {
                                        String splitTemp2[] = splitTemp[i].split(":");
                                        mensagensBeaconsAntecessoresChaveValor.put(splitTemp2[0], splitTemp2[1]);
                                    }

                                    // if(!beaconsAntecessoresLista.contains(ultimoBeaconMacAddress)) {
                                    //   mensagemFinalAoUsuario = mensagensBeaconsAntecessoresChaveValor.get("CaminhoEntrada");
                                    //} else {
                                    //    mensagemFinalAoUsuario = mensagensBeaconsAntecessoresChaveValor.get("CaminhoSaida");
                                    //}

                                    //restJson.setText("Beacon Atual: " + beaconMacAddress + "\n\nBeacon Anterior: " + ultimoBeaconMacAddress
                                    //        + "\n\n Mensagem: \n " + mensagemFinalAoUsuario);

                                    Toast.makeText(context, mensagemBeacon, Toast.LENGTH_LONG).show();

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        },

                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                //restJson.setText(error.toString());
                            }
                        }


                );

                VolleyApplication.getInstance().getRequestQueue().add(request);

            }

        };
        timer.schedule(task,5000,5000);
    }

}
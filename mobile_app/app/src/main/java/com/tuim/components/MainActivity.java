package com.tuim.components;

import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.*;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import com.tuim.components.pojo.ComponentPOJO;
import com.tuim.components.pojo.ComponentRequestPOJO;
import com.tuim.components.pojo.ResponsePOJO;
import org.json.JSONObject;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {

    private PackageManager packageManager = null;
    private List<ApplicationInfo> applist = null;
    private ApplicationAdapter listadaptor = null;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    public JSONObject json = new JSONObject();
    private ListView mListView;

    ArrayList<ComponentPOJO> rawComponents = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);

        packageManager = getPackageManager();

        new LoadApplications().execute();

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeToRefresh);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                SendJsonTask task = new SendJsonTask();
                task.execute();

                listadaptor.notifyDataSetChanged();

                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }


    protected ListView getListView() {
        if (mListView == null) {
            mListView = (ListView) findViewById(android.R.id.list);
        }
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> l, View v, int position, long id) {

                //super.onItemClick(l, v, position, id);

                ApplicationInfo app = applist.get(position);
                try {
                    displayMoreInfoDialog(app);

                } catch (ActivityNotFoundException e) {
                    Toast.makeText(MainActivity.this, e.getMessage(),
                            Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    Toast.makeText(MainActivity.this, e.getMessage(),
                            Toast.LENGTH_LONG).show();
                }

            }
        });
        return mListView;
    }

    protected void setListAdapter(ListAdapter adapter) {
        getListView().setAdapter(adapter);
    }

    protected ListAdapter getListAdapter() {
        ListAdapter adapter = getListView().getAdapter();
        if (adapter instanceof HeaderViewListAdapter) {
            return ((HeaderViewListAdapter)adapter).getWrappedAdapter();
        } else {
            return adapter;
        }
    }


    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        boolean result = true;

        switch (item.getItemId()) {
            case R.id.menu_about: {
                displayAboutDialog();

                break;
            }
            default: {
                result = super.onOptionsItemSelected(item);

                break;
            }
        }

        return result;
    }

    private void displayMoreInfoDialog(final ApplicationInfo app) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View mView = getLayoutInflater().inflate(R.layout.app_info, null);
        //builder.setTitle( app.loadLabel(packageManager) );

        View mGoogle = mView.findViewById(R.id.buttons);
        ImageView mIcon = (ImageView) mView.findViewById(R.id.app_icon);
        TextView mTitle = (TextView) mView.findViewById(R.id.app_title);
        TextView mPackage = (TextView) mView.findViewById(R.id.package_name);
        TextView mVersion = (TextView) mView.findViewById(R.id.version);
        TextView mStatus = (TextView) mView.findViewById(R.id.status);
        View mApplicationData = mView.findViewById(R.id.app_data);
        TextView mApplicationDate = (TextView) mView.findViewById(R.id.app_date);
        View mApplicationDateSeparator = mView.findViewById(R.id.app_date_sep);
        TextView mPlayLinked = (TextView) mView.findViewById(R.id.play_linked);
        TextView mStopApplication = (TextView) mView.findViewById(R.id.stop_application);
        TextView mUninstallApplication = (TextView) mView.findViewById(R.id.uninstall_application);
        TextView mStartApplication = (TextView) mView.findViewById(R.id.start_application);
        TextView mApplicationPermissions = (TextView) mView.findViewById(R.id.permissions);
        View mPlayIcon = mView.findViewById(R.id.play_icon);
        View mPlay = mView.findViewById(R.id.play);

        mIcon.setImageDrawable(app.loadIcon(packageManager));
        mTitle.setText( app.loadLabel(packageManager) );
        mPackage.setText( app.packageName );

        boolean isFromGPlay = false;
        try {
            PackageManager pm = getPackageManager();
            PackageInfo packageInfo = null;
//IF IS INSTALLED
            boolean installed = false;
            try {
                pm.getPackageInfo(app.packageName, PackageManager.GET_ACTIVITIES);
                installed = true;
                mStatus.setText("Application installed");
            } catch (PackageManager.NameNotFoundException e) {
                installed = false;
                mStatus.setText("Application NOT installed");
            }
            Log.d("isInstalled", String.valueOf(installed));


            if(installed == false) {
                mPlay.setVisibility(View.GONE);
                mPlayIcon.setVisibility(View.GONE);
                mVersion.setVisibility(View.INVISIBLE);
                mStatus.setText(R.string.app_info_not_installed);
                mApplicationData.setVisibility(View.GONE);
                mPlayLinked.setVisibility(View.GONE);
                mStopApplication.setEnabled(false);
                mStopApplication.setBackgroundColor(Color.parseColor("#C5CAE9"));
                mUninstallApplication.setEnabled(false);
                mUninstallApplication.setBackgroundColor(Color.parseColor("#C5CAE9"));
                mStartApplication.setEnabled(false);
                mStartApplication.setBackgroundColor(Color.parseColor("#C5CAE9"));
            } else {
                ApplicationInfo appInfo = pm.getApplicationInfo(app.packageName, 0);

//VERSION
                String versionName = getVersion("name", app);
                String versionCode = getVersion("code", app);

                mVersion.setText(getString(R.string.app_info_version, versionName, versionCode));

//PERMISSIONS

                List<String> permissions = new ArrayList<>();
                permissions = getPermissions(pm, app);
                mApplicationPermissions.setText( permissions.toString() );
//LIFE
                boolean isRunning = getActivity(app);

//CONTENT PROVIDER
                packageInfo = pm.getPackageInfo(app.packageName, PackageManager.GET_PROVIDERS);
                ProviderInfo[] providers = packageInfo.providers;
                if (providers != null) {
                    for (ProviderInfo provider : providers) {
                        Log.d("PROVIDER", provider.authority);
                        if (provider.authority.contains("google")) {
                            isFromGPlay = true;
                            mPlayLinked.setText( "Provider: Google Play" );
                            break;
                        } else {
                            mPlayLinked.setText( "Provider: " + provider.authority );
                        }
                    }
                } else {
                    mPlayLinked.setText( "Provider: Unknown" );
                }
                if(isFromGPlay == false) {
                    mPlayIcon.setVisibility(mView.GONE);
                    mPlay.setVisibility(mView.GONE);
                } else {
                    mPlay.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + app.packageName)));
                            } catch (android.content.ActivityNotFoundException anfe) {
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + app.packageName)));
                            }
                        }
                    });
                }
//INSTALLED DATA
                Date installedDate = getDate("installed", appInfo, packageInfo);
                Date modifiedDate = getDate("modified", appInfo, packageInfo);
                final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
                mApplicationDate.setText("Installed: " + dateFormat.format(installedDate) + "\n" + "Modified: " + dateFormat.format(modifiedDate));

//BUTTONS
                if (isRunning) {
                    mStartApplication.setEnabled(false);
                    mStartApplication.setBackgroundColor(Color.parseColor("#C5CAE9"));
                    mUninstallApplication.setEnabled(false);
                    mUninstallApplication.setBackgroundColor(Color.parseColor("#C5CAE9"));
                    mStopApplication.setEnabled(true);
                    mStopApplication.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ActivityManager am = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
                            try {
                                am.killBackgroundProcesses(app.packageName);
                                Toast.makeText(getBaseContext(), "Success", Toast.LENGTH_LONG).show();
                            } catch (Exception e) {
                                Log.d("error: ", e.toString());
                                Toast.makeText(getBaseContext(), "You cannot stop this app", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                } else {
                    mStartApplication.setEnabled(true);
                    mStartApplication.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = packageManager.getLaunchIntentForPackage(app.packageName);

                            if (null != intent) {
                                startActivity(intent);
                            }
                        }
                    });
                    mUninstallApplication.setEnabled(true);
                    mUninstallApplication.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(Intent.ACTION_DELETE);
                            intent.setData(Uri.parse("package:" + app.packageName));
                            startActivity(intent);
                        }
                    });
                    mStopApplication.setEnabled(false);
                    mStopApplication.setBackgroundColor(Color.parseColor("#C5CAE9"));
                }

            }

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        builder.setPositiveButton("Open App", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Intent intent = packageManager.getLaunchIntentForPackage(app.packageName);

                if (null != intent) {
                    startActivity(intent);
                }
                dialog.cancel();
            }
        });

        builder.setNegativeButton("Back", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        builder.setView(mView);
        builder.show();
    }

    private class LoadApplications extends AsyncTask<Void, Void, Void> {
        private ProgressDialog progress = null;

        @Override
        protected Void doInBackground(Void... params) {
            rawComponents = new ArrayList<>();
            applist = checkForLaunchIntent(packageManager.getInstalledApplications(PackageManager.GET_META_DATA));
            // JSON create start
            for ( ApplicationInfo app : applist ) {
                ComponentPOJO component = new ComponentPOJO();
                //get app name
                final PackageManager pm = getApplicationContext().getPackageManager();
                ApplicationInfo ai;
                PackageInfo packageInfo = null;
                try {
                    ai = pm.getApplicationInfo( app.packageName, 0);
                    packageInfo = pm.getPackageInfo(app.packageName, 0);
                } catch (final PackageManager.NameNotFoundException e) {
                    ai = null;
                }
                final String applicationName = (String) (ai != null ? pm.getApplicationLabel(ai) : "(unknown)");

                try {
                    component.setName(applicationName);
                    component.setPackageName( app.packageName );
                    component.setVersionName( getVersion("name", app) );
                    component.setVersionNumber( getVersion("code", app) );
                    component.setPermissions( getPermissions(pm, app) );
                    component.setActivity(String.valueOf(getActivity(app)));
                    component.setInstalledData( getDate("installed", ai, packageInfo) );
                    component.setModifiedData( getDate("modified", ai, packageInfo) );

                    List<String> listProviders = new ArrayList<>();
                    packageInfo = pm.getPackageInfo(app.packageName, PackageManager.GET_PROVIDERS);
                    ProviderInfo[] providers = packageInfo.providers;
                    if (providers != null) {
                        for (ProviderInfo provider : providers) {
                            listProviders.add( provider.authority );
                        }
                    } else {
                        listProviders.add( "Unknown" );
                    }
                    component.setContentProviders( listProviders );
                    rawComponents.add( component );

                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }

            }

            // JSON create end

            listadaptor = new ApplicationAdapter(MainActivity.this,
                    R.layout.activity_main, applist);

            return null;
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

        @Override
        protected void onPostExecute(Void result) {
            setListAdapter(listadaptor);
            progress.dismiss();
            super.onPostExecute(result);
        }

        @Override
        protected void onPreExecute() {
            progress = ProgressDialog.show(MainActivity.this, null,
                    "Loading application info...");
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }
    }

    private Date getDate(String option, ApplicationInfo appInfo, PackageInfo packageInfo) {
        String appFile = appInfo.sourceDir;
        long modified = new File(appFile).lastModified();
        Date modifiedDate = new Date( modified );
        Date installedDate = new Date(packageInfo.firstInstallTime);
//        Log.d("INSTALLED", "date: " + installedDate);
//        Log.d("LAST MODIFIED", "date " + modifiedDate);
        if(option.equals("installed"))
            return installedDate;
        else if (option.equals("modified"))
            return modifiedDate;
        return null;
    }

    private boolean getActivity(ApplicationInfo app) {
        ActivityManager am = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> activeApps= am.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo appProcess : activeApps) {
            if (appProcess.processName.equals(app.packageName)) {
                //Log.i("PROCESS CODE",String.valueOf(appProcess.importance));
                return true;
            }
        }
        return false;
    }

    private List<String> getPermissions(PackageManager pm, ApplicationInfo app) throws PackageManager.NameNotFoundException {
        PackageInfo packageInfo = pm.getPackageInfo(app.packageName, PackageManager.GET_PERMISSIONS);
        String[] requestedPermissions = packageInfo.requestedPermissions;
        List<String> permissions = new ArrayList<>();
        if(requestedPermissions != null) {
            for (int i = 0; i < requestedPermissions.length; i++) {
                //Log.d("test", requestedPermissions[i]);
                permissions.add(requestedPermissions[i]);
            }
        }
        return permissions;
    }

    private String getVersion(String name, ApplicationInfo app) throws PackageManager.NameNotFoundException {
        PackageInfo packageInfo = getPackageManager().getPackageInfo(app.packageName, 0);
        if ( name.equals("name") )
            return packageInfo.versionName.toString();
        else if ( name.equals("code") )
            return String.valueOf(packageInfo.versionCode);
        return null;
    }

    private void displayAboutDialog() {
        //final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MainActivity.this );
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
        builder.setTitle("Credentials");
        builder.setMessage("Kowalewska Agata \nFilip Ignasiak \n\n2017. Military University of Technology in Warsaw.");

        builder.setPositiveButton("Great!", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private List<ApplicationInfo> checkForLaunchIntent(List<ApplicationInfo> list) {
        ArrayList<ApplicationInfo> applist = new ArrayList<ApplicationInfo>();
        for (ApplicationInfo info : list) {
            try {
                if (null != packageManager.getLaunchIntentForPackage(info.packageName)) {
                    applist.add(info);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return applist;
    }

    private class SendJsonTask extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... params) {
            try {
                SharedPreferences serverName = getSharedPreferences("AUTHENTICATION_FILE_NAME", Context.MODE_PRIVATE);
                Looper.prepare();
                final String url = serverName.getString("servername", "") + "/service";
                ComponentRequestPOJO components = new ComponentRequestPOJO();

                SharedPreferences userName = getSharedPreferences("AUTHENTICATION_FILE_NAME", Context.MODE_PRIVATE);
                components.setAuthor( userName.getString("username", "") );
                components.setApplications(rawComponents);
                RestTemplate restTemplate = new RestTemplate(getClientHttpRequestFactory());
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                ResponsePOJO response = new ResponsePOJO();
                try {
                    response = restTemplate.postForObject(url + "/upload", components, ResponsePOJO.class);
                    Log.d("SUCCESS: ", "");
                    Toast.makeText(getBaseContext(), response.getResult()+": "+response.getDescription(), Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    Log.d("FAILED", e.toString());
                    Toast.makeText(getBaseContext(), "Failed: something went wrong", Toast.LENGTH_LONG).show();
                }
            } catch (Exception e ) {
                Log.d("FAILED", e.toString());
                Toast.makeText(getBaseContext(), "Failed: connection", Toast.LENGTH_LONG).show();
            }
            Looper.loop();
            return null;
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

        private ClientHttpRequestFactory getClientHttpRequestFactory() {
            int timeout = 3000;
            HttpComponentsClientHttpRequestFactory clientHttpRequestFactory =
                    new HttpComponentsClientHttpRequestFactory();
            clientHttpRequestFactory.setConnectTimeout(timeout);
            clientHttpRequestFactory.setReadTimeout(timeout);
            return clientHttpRequestFactory;
        }
    }

}
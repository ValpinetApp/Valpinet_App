# Valpinet_App
code pour Antoine:
final LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE ); //on recupere l etat du gps pour savoir si il est actif ou non
        if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) )
        {        Button fra = findViewById(R.id.button2);


            createGpsDisabledAlert();
        }
private void createGpsDisabledAlert() {
        AlertDialog.Builder localBuilder = new AlertDialog.Builder(this);
        localBuilder
                .setMessage("Le GPS est inactif, voulez-vous l'activer ?")
                .setCancelable(false)
                .setPositiveButton("Activer GPS ",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                                MainActivity.this.showGpsOptions();
                            }
                        }
                );
        localBuilder.setNegativeButton("Ne pas l'activer ",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        paramDialogInterface.cancel();
                    }
                }
        );
        localBuilder.create().show();
    }

    private void showGpsOptions() {
        startActivityForResult(new Intent("android.settings.LOCATION_SOURCE_SETTINGS"),-1);
    }

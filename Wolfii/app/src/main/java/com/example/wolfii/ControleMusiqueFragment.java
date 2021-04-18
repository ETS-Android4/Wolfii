package com.example.wolfii;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.util.concurrent.TimeUnit;

import static com.example.wolfii.MainActivity.mBound;
import static com.example.wolfii.MainActivity.mService;



/*A FAIRE :
 *
 * Ajouter images des mp3 sur notification
 * Ainsi que maj notif
 * Et ajout nom musique sur notif
 * Ajouter la maj de la notification et interface sur appui du bouton DemaPause ainsi que BoucleDeboucle
 * Remplacer musique arret par une autre fonction qui arrête pas la notif etc... quand on appuit sur le bouton "MusiqueSuivante" ou "musiquePrecedente"
 * Revoir la mise à jour de la page contrôle musique quand on va dessus (au démarrage etc...)
 * Modifier et rajouter l'arrêt de la musique avec un pour arreter la musique et l'autre pour arrêter la musique et l'autre pour tout arrêter (notif, foreground, focus etc...).
 *
 */


public class ControleMusiqueFragment extends Fragment {

    private SeekBar seekBarMusique;                             //SeekBar de lecture de la musique

    private TextView txtViewMusiqueTemps, txtViewMusiqueDuree;   //TextView du temps de lecture de la musique

    private Button cmdDemaPause, cmdArret,cmdPrecedent,cmdSuivant,cmdRejouer;  //boutons de la page


    private static final String ACTION_STRING_ACTIVITY = "ToActivity";  //Action pour envoyer un Boradcast dans l'activité



    /*------------------------------------------FONCTION ONCREATE-----------------------------------------------------*/
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_controle_musique, container, false);

        //Liaisons des Boutons, des TextViews et du SeekBar de l'interface dans la code.
        this.txtViewMusiqueTemps = (TextView) root.findViewById(R.id.txtViewMusiqueTemps);
        this.txtViewMusiqueTemps.setSoundEffectsEnabled(false);

        this.txtViewMusiqueDuree = (TextView) root.findViewById(R.id.txtViewMusiqueDuree);
        this.txtViewMusiqueDuree.setSoundEffectsEnabled(false);



        this.cmdDemaPause = (Button) root.findViewById(R.id.btnDemaPause);
        this.cmdDemaPause.setSoundEffectsEnabled(false);
        this.cmdDemaPause.setOnClickListener(new EcouteurBtnDemaPause());

        this.cmdArret = (Button) root.findViewById(R.id.btnArret);
        this.cmdArret.setSoundEffectsEnabled(false);
        this.cmdArret.setOnClickListener(new EcouteurBtnArret());


/*        this.cmdPrecedent = (Button) root.findViewById(R.id.btnPrecedent);
        this.cmdPrecedent.setSoundEffectsEnabled(false);
        this.cmdPrecedent.setOnClickListener(new cmdBtnPrecedentEcouteur());

        this.cmdSuivant = (Button) root.findViewById(R.id.btnSuivant);
        this.cmdSuivant.setSoundEffectsEnabled(false);
        this.cmdSuivant.setOnClickListener(new cmdBtnSuivantEcouteur());*/


        this.cmdRejouer = (Button) root.findViewById(R.id.btnRejouer);
        this.cmdRejouer.setSoundEffectsEnabled(false);
        this.cmdRejouer.setOnClickListener(new EcouteurBtnRejouer());

        this.seekBarMusique=(SeekBar) root.findViewById(R.id.seekBarMusique);
        this.seekBarMusique.setSoundEffectsEnabled(false);
        this.seekBarMusique.setOnSeekBarChangeListener(new EcouteurSeekBar());

        IntentFilter intentFilter = new IntentFilter(ACTION_STRING_ACTIVITY);
        getActivity().registerReceiver(broadcastReceiverMajInterface, intentFilter);

        return root;
    }


    /*--------------------------------------FONCTION ONSTART------------------------------------------------*/
    @Override
    public void onStart() {
        super.onStart();
    }


    /*--------------------------------------FONCTION ONDESTROY------------------------------------------------*/
    @Override
    public void onDestroy() {
        super.onDestroy();
        //Arrêt broadcast receiver de mise à jour de l'interface
        getActivity().unregisterReceiver(broadcastReceiverMajInterface);
    }

    /*--------------------------------------FONCTION/CLASS SEEKBAR------------------------------------------------*/

    private class EcouteurSeekBar implements SeekBar.OnSeekBarChangeListener {

        //Evenement qui s'enclenche sur le déplacement du seekbar
        public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser)
        {
            if (fromUser && mBound && mService.getMusiquePlayerIsSet()) {
                mService.setMusiquePlayerPosition(progress);
                txtViewMusiqueTemps.setText(millisecondesEnMinutesSeconde(mService.getMusiquePlayerPosition()));
            }
        }

        //Evenement qui s'enclenche sur l'appuit sur le seekbar
        public void onStartTrackingTouch(SeekBar seekBar) {}

        //Evenement qui s'enclenche sur la fin du déplacement du seekbar
        public void onStopTrackingTouch(SeekBar seekBar) {}
    }



    /*--------------------------------------FONCTION BOUTONS------------------------------------------------*/

    private class EcouteurBtnDemaPause implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            mService.musiqueDemaPause();
            seekBarMusique.setMax(mService.getMusiquePlayerDuration());
        }
    }

    private class EcouteurBtnArret implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            mService.arretTotal();
            seekBarMusique.setProgress(0);
            txtViewMusiqueTemps.setText("00:00");
        }
    }

    private class EcouteurBtnRejouer implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            //Active désactive la boucle de la musique actuelle
            mService.musiqueBoucleDeboucle();
        }
    }


/*    public void cmdMusiqueStuivante(View view)
    {

    }

    public void cmdMusiquePrecedente(View view)
    {

    }*/



    /*----------------------------------GESTION BROADCASTRECEIVER--------------------------------------------------*/

    private BroadcastReceiver broadcastReceiverMajInterface = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            majInterface();//Mise à jour de l'interface
        }
    };

    /*--------------------------------------FONTION MAJ INTERFACE---------------------------------------------------*/

    public void majInterface()
    {
        if (mService.getMusiquePlayerIsPlaying())
        {
            //seekBarMusique.setMax(mService.getMusiquePlayerDuration());
            //txtViewMusiqueDuree.setText(millisecondesEnMinutesSeconde(mService.getMusiquePlayerDuration()));
            seekBarMusique.setMax(mService.getMusiquePlayerDuration());
            txtViewMusiqueDuree.setText(millisecondesEnMinutesSeconde(mService.getMusiquePlayerDuration()));
            seekBarMusique.setProgress(mService.getMusiquePlayerPosition());
            txtViewMusiqueTemps.setText(millisecondesEnMinutesSeconde(mService.getMusiquePlayerPosition()));
        }
    }

    /*--------------------------------------AUTRES FONCTIONS------------------------------------------------*/

    @SuppressLint("DefaultLocale")
    private String millisecondesEnMinutesSeconde(int tmpsMillisecondes) {
        return String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(tmpsMillisecondes),
                TimeUnit.MILLISECONDS.toSeconds(tmpsMillisecondes) - TimeUnit.MILLISECONDS.toMinutes(tmpsMillisecondes) * 60);
    }
}
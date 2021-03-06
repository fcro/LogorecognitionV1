package com.telecom.cottoncrosnier.logorecognition.service;

import android.app.IntentService;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Process;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.telecom.cottoncrosnier.logorecognition.Utils;
import com.telecom.cottoncrosnier.logorecognition.image.ImageUtils;
import com.telecom.cottoncrosnier.logorecognition.image.MatManager;
import com.telecom.cottoncrosnier.logorecognition.reference.Brand;
import com.telecom.cottoncrosnier.logorecognition.reference.BrandList;
import com.telecom.cottoncrosnier.logorecognition.reference.RefDescriptors;

import java.io.File;

/**
 * Classe de service contenant les traitement de comparaison d'images.
 */
public class AnalyzePhotoService extends IntentService {

    private static final String TAG = AnalyzePhotoService.class.getSimpleName();

    public static final String ANALYZE_RESULT = "ANALYZE_RESULT";

    public static final String BROADCAST_ACTION_ANALYZE = "BROADCAST_ACTION_ANALYZE";


    /**
     * Instancie AnalyzePhotoService.
     */
    public AnalyzePhotoService() {
        super("AnalyzePhotoService");
    }


    /**
     * <p>Méthode appelée par startService() pour démarrer le traitement du service.</p>
     * <p>Redimensionne l'image contenue en data dans {@code intent} pour créer un {@link MatManager},
     * puis appelle {@link AnalyzePhotoService#handleActionAnalyze(MatManager)}.</p>
     *
     * @param intent Intent défini dans la classe appelante.
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            Process.setThreadPriority(Process.THREAD_PRIORITY_URGENT_DISPLAY);

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            Bitmap bitmap = ImageUtils.scaleBitmapDown(BitmapFactory.decodeFile(intent.getData().getPath(), options), 500);

            File bitmapFile = Utils.bitmapToCache(getBaseContext(), bitmap, intent.getData().getLastPathSegment());

            final MatManager matManager;
            if (bitmapFile != null) {
                matManager = new MatManager(bitmapFile.getPath());
                handleActionAnalyze(matManager);
            }
        }
    }

    /**
     * <p>Pour chaque {@link Brand} de {@link BrandList}, compare le descripeur de
     * {@code matManger} avec les descripteurs correspondants dans {@link RefDescriptors}.</p>
     * <p>Le {@link Brand} le plus proche est défini par le descripteur qui a le plus de matches
     * avec celui de {@code matManager}.</p>
     * <p>Si aucun match n'est trouvé, le {@link Brand} le plus proche est null.</p>
     * <p>Après analyse, le {@link Brand} le plus proche est broadcasté au LocalBroadcastManager.</p>
     *
     * @param matManager image à analyser.
     */
    private void handleActionAnalyze(final MatManager matManager) {
        Brand bestBrand = null;
        long bestMatches = 0;

        for (Brand brand : BrandList.getBrands()) {
            Log.d(TAG, "handleActionAnalyze:: bestBrand = " + bestBrand +
                    " bestMatches = " + bestMatches);
            long matches =
                    matManager.getMatchesWith(RefDescriptors.getDescriptors(brand.getBrandName()));
            Log.d(TAG, "handleActionAnalyze:: brand = " + brand.getBrandName() +
                    " matches = " + matches);
            if (matches > bestMatches) {
                bestMatches = matches;
                bestBrand = brand;
            }
        }

        Log.d(TAG, "handleActionAnalyze: broadcasting bestBrand = " + bestBrand);
        Intent localIntent = new Intent(BROADCAST_ACTION_ANALYZE).putExtra(ANALYZE_RESULT, bestBrand);
        LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
    }

}

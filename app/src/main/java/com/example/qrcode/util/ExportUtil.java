package com.example.qrcode.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import androidx.core.content.FileProvider;
import com.example.qrcode.model.QRCodeModel;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ExportUtil {
    public static void exportToCSV(Context context, List<QRCodeModel> list) {
        StringBuilder csv = new StringBuilder();
        csv.append("ID,Content,Type,Date,IsScanned\n");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

        for (QRCodeModel model : list) {
            csv.append(model.getId()).append(",")
               .append("\"").append(model.getContent().replace("\"", "\"\"")).append("\",")
               .append(model.getType()).append(",")
               .append(sdf.format(new Date(model.getTimestamp()))).append(",")
               .append(model.isScanned()).append("\n");
        }

        try {
            File file = new File(context.getCacheDir(), "history.csv");
            FileWriter writer = new FileWriter(file);
            writer.write(csv.toString());
            writer.close();

            Uri contentUri = FileProvider.getUriForFile(context, context.getPackageName() + ".fileprovider", file);
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/csv");
            intent.putExtra(Intent.EXTRA_STREAM, contentUri);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            context.startActivity(Intent.createChooser(intent, "Export History"));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

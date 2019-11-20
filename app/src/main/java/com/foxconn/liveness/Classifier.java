package com.foxconn.liveness;

import android.graphics.Bitmap;
import android.util.Log;
import java.lang.Math;

import org.pytorch.Tensor;
import org.pytorch.Module;
import org.pytorch.IValue;
import org.pytorch.torchvision.TensorImageUtils;


public class Classifier {

    Module model;
    float[] mean = {0.485f, 0.456f, 0.406f};
    float[] std = {0.229f, 0.224f, 0.225f};

    public Classifier(String modelPath){

        model = Module.load(modelPath);
    }

    public void setMeanAndStd(float[] mean, float[] std){
        this.mean = mean;
        this.std = std;
    }

    public Tensor preprocess(Bitmap bitmap, int wsize, int hsize){

        bitmap = Bitmap.createScaledBitmap(bitmap,wsize,hsize,false);
        return TensorImageUtils.bitmapToFloat32Tensor(bitmap,this.mean,this.std);
    }

    public int argMax(float[] inputs){

        int maxIndex = -1;
        float maxvalue = 0.0f;

        for (int i = 0; i < inputs.length; i++){
            Log.e("pytorchandroid", i+ ":"+inputs[i]);
            if(inputs[i] > maxvalue) {
                maxIndex = i;
                maxvalue = inputs[i];
            }
        }

        return maxIndex;
    }

    public float fmp_predict(float[] inputs){
        double total = Math.exp(inputs[0]) + Math.exp(inputs[1]);
        //double fmp = Math.exp(inputs[0])/ total;
        float fmp =  (float)(Math.exp(inputs[0])/ total);
        return fmp;
    }

    public String predict(Bitmap bitmap){

        Tensor tensor = preprocess(bitmap,64, 64);

        IValue inputs = IValue.from(tensor);
        Tensor outputs = model.forward(inputs).toTensor();

        float[] scores = outputs.getDataAsFloatArray();

        //int classIndex = argMax(scores);
        float fmp = fmp_predict(scores);
        int classIndex = 0;
        if (fmp > 0.96) {
            classIndex = 0;
        } else {
            classIndex = 1;
        }
        String fmp_string = String.format("  非活體機率 %.3f", fmp);
        return Constants.IMAGENET_CLASSES[classIndex] + fmp_string;
    }
}
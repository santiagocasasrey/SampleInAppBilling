package com.scr.sampleinappbilling;



import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.scr.sampleinappbilling.utilsbilling.IabHelper;
import com.scr.sampleinappbilling.utilsbilling.IabResult;
import com.scr.sampleinappbilling.utilsbilling.Inventory;
import com.scr.sampleinappbilling.utilsbilling.Purchase;

public class MainActivity extends ActionBarActivity implements OnClickListener{

	
	private Button btnAccion;
	private Button btnComprar;
	
	private final String BASE64ENCODEDPUBLICKEY = "Your Google Play Key";
	static final String ITEM_SKU = "android.test.purchased";
	private static final String TAG = "com.scr.sampleinappbilling";
	private boolean billingDisponible = false;
	
	private IabHelper mHelper;
	private IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener;
	private IabHelper.QueryInventoryFinishedListener mReceivedInventoryListener;
	private IabHelper.OnConsumeFinishedListener mConsumeFinishedListener;
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
	            WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_main);		
		
		mHelper = new IabHelper(this, BASE64ENCODEDPUBLICKEY.trim());
		
		// Startup
		mHelper.startSetup(new 
		IabHelper.OnIabSetupFinishedListener() {
			public void onIabSetupFinished(IabResult result) 
			{
				if (!result.isSuccess()) {
				  billingDisponible = false;
				  Log.d(TAG, "In-app Billing setup failed: " + 
				result);
				} else {       
					billingDisponible = true;
					
				    Log.d(TAG, "In-app Billing is set up OK");
				}
			}
		});
		
		//Purchase
			mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
				public void onIabPurchaseFinished(IabResult result, 
			                    Purchase purchase) 
				{
					Log.i(TAG, "onIabPurchaseFinished");
					
				   if (result.isFailure()) {
					   
					   if (result.getResponse() == 7){		
						   Log.i(TAG, "Ya Comprado");
						   setEnabledAccion(true);
						   Toast.makeText(getApplicationContext(), "El producto ya ha sido comprado.\nGracias", Toast.LENGTH_SHORT).show();
					   }
				      
				      return;
					 }      
					 else if (purchase.getSku().equals(ITEM_SKU)) {
						 Log.i(TAG, "Comprado");
						 setEnabledAccion(true);
						 Toast.makeText(getApplicationContext(), "Gracias por comprar.", Toast.LENGTH_SHORT).show();
					 }
				      
			     }
			
			};
			
			//Consumir producto
			mReceivedInventoryListener  = new IabHelper.QueryInventoryFinishedListener() {
				   public void onQueryInventoryFinished(IabResult result,
				      Inventory inventory) {
					   		   
				      if (result.isFailure()) {
					  // Handle failure
				      } else {
				    	  Log.i(TAG, "Consumido");
			              mHelper.consumeAsync(inventory.getPurchase(ITEM_SKU), 
						mConsumeFinishedListener);
			             setEnabledAccion(false);
			                 
				      }
			    }
			};
	
		btnAccion = (Button) findViewById(R.id.btnAccion);
		btnAccion.setOnClickListener(this);	
		btnAccion.setEnabled(false);
		
		btnComprar = (Button) findViewById(R.id.btnComprar);	
		btnComprar.setOnClickListener(this);
	
	}
	
	
	public void launchPurchase(){
		Log.i(TAG, "launchPurchase");
		if (billingDisponible)
		{
			mHelper.launchPurchaseFlow(this, ITEM_SKU, 10002,   
		   			   mPurchaseFinishedListener, "Test");
		}else{
			Toast.makeText(this, "No se ha podido conectar con el servidor de Google.\nIntentelo de nuevo en unos instantes.", Toast.LENGTH_LONG).show();
		}
		
	}
	
	public void consumeItem() {
		Log.i(TAG, "consumeItem");
		mHelper.queryInventoryAsync(mReceivedInventoryListener);
	}
	
	
	public boolean devBillingDisponible(){
		return this.billingDisponible;
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, 
	     Intent data) 
	{
		
		Log.i(TAG, "onActivityResult");
		
	      if (!mHelper.handleActivityResult(requestCode, 
	              resultCode, data)) {     
	    	
	      }
	}	
	
	@Override
	public void onClick(View v) {
		
		
		if (v.getId() == R.id.btnAccion)
		{
			consumeItem();
			
			AlertDialog.Builder dialog = new AlertDialog.Builder(this);  
	        dialog.setTitle("SampleInAppBilling");  
	        dialog.setMessage("Acabas de consumir tu compra.\nGracias."); 
	        dialog.setCancelable(false);  
	        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener()           {   
	            @Override
				public void onClick(DialogInterface dialogo, int id) {  
	            	 	
	            }  
	        });  
	        dialog.show(); 
			
		}else if (v.getId() == R.id.btnComprar)
		{		
			launchPurchase();
		}
	}
	
	private void setEnabledAccion(boolean enabled){
		btnAccion.setEnabled(enabled);
	}
	

	
}

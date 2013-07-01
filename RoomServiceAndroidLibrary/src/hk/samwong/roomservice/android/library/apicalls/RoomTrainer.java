package hk.samwong.roomservice.android.library.apicalls;

import hk.samwong.roomservice.android.library.helpers.TrainingDataAccumulator;
import hk.samwong.roomservice.commons.dataFormat.Response;
import hk.samwong.roomservice.commons.dataFormat.WifiInformation;
import hk.samwong.roomservice.commons.parameterEnums.ReturnCode;

import java.util.List;

import android.app.Activity;
import android.os.AsyncTask;

/**
 * Wrapped around the asynctask to make the method name make more sense.
 * In a nut shell, we start collecting fingerprints once it is "executed"
 * then we stop collecting fingerprints with "cancel"
 * and "cancel" will trigger the onCancel method which submits the result.
 * @author wongsam
 *
 */

public abstract class RoomTrainer{
	
	private RoomTrainer parent = this;
	private String room = null;
	private Activity activity;
	private TrainingDataAccumulator trainingDataAccumulator;
	private Response response;
	private List<WifiInformation> collectedfingerprints = null;
	
	public RoomTrainer(Activity activity) {
		this.activity =  activity;		
	}
	
	private AsyncTask<Void, Void, Void> roomTrainer = new AsyncTask<Void, Void, Void>() {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			trainingDataAccumulator = new TrainingDataAccumulator(room) {
				@Override
				protected void onProgressUpdate(WifiInformation... values) {
					if(roomTrainer.isCancelled()){
						this.cancel(false);
					}
					super.onProgressUpdate(values);
					parent.onProgressUpdate(values);
				}
				@Override
				protected void onCancelled(List<WifiInformation> result) {
					collectedfingerprints = result;
				}
			};
		}
		
		@Override
		protected Void doInBackground(Void... dontcare) {
			// Start collecting data as soon as it is executed
			trainingDataAccumulator.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, activity);
			return null;
		}
		
		
	};
	
	/**
	 * Set the label to name, and submit the data. 
	 * Handle the result using onPostExecute
	 * @param name
	 */
	public RoomTrainer setRoomLabel(String name){
		room = name;
		return this;
	}
	
	public String getRoomLabel(){
		return room;
	}
	
	@SuppressWarnings("unchecked")
	public RoomTrainer submit(){
		stopCollection();
		if(room == null){
			response = new Response().setReturnCode(ReturnCode.ILLEGAL_ARGUMENT).setExplanation("Room name not specified");
			onPostExecute(response);
			return this;
		}
		if(collectedfingerprints == null || collectedfingerprints.size() == 0){
			response = new Response().setReturnCode(ReturnCode.NO_RESPONSE).setExplanation("No wifi finerprints collected");
			onPostExecute(response);
			return this;
		}
		new SubmitBatchTrainingData(room, activity) {
			@Override
			protected void onPostExecute(Response result) {
				parent.onPostExecute(result);
			}
		}.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, collectedfingerprints);
		return this;
	}
	
	public RoomTrainer stopCollection(){
		roomTrainer.cancel(false);
		return this;
	}
	
	protected abstract void onProgressUpdate(WifiInformation[] values);

	public RoomTrainer beginCollection(){
		roomTrainer.execute();
		return this;
	}
	
	protected abstract void onPostExecute(Response result);
	
	

}

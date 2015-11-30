package com.technisat.radiotheque.update;


public interface OnUpdateResult {
	
	/**
	 * Event fired when download was successfully completed.
	 * @param content The content that has been Updated.
	 */
	public void onUpdateSuccess();
	
}

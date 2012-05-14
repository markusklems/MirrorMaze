package edu.kit.aifb.mirrormaze.client;

import gwtupload.client.IUploadStatus.Status;
import gwtupload.client.IUploader;
import gwtupload.client.IUploader.OnFinishUploaderHandler;
import gwtupload.client.IUploader.UploadedInfo;
import gwtupload.client.MultiUploader;

import org.swfupload.client.SWFUpload;
import org.swfupload.client.SWFUpload.ButtonAction;
import org.swfupload.client.UploadBuilder;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.RootPanel;
import com.smartgwt.client.types.Encoding;
import com.smartgwt.client.types.FormMethod;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.UploadItem;
import com.smartgwt.client.widgets.form.fields.events.KeyPressEvent;
import com.smartgwt.client.widgets.form.fields.events.KeyPressHandler;
import com.smartgwt.client.widgets.layout.Layout;
import com.smartgwt.client.widgets.layout.VLayout;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class MirrorMaze implements EntryPoint {
	/**
	 * The message displayed to the user when the server cannot be reached or
	 * returns an error.
	 */
	private static final String SERVER_ERROR = "An error occurred while "
			+ "attempting to contact the server. Please check your network "
			+ "connection and try again.";

	/**
	 * Create a remote service proxy to talk to the server-side Greeting
	 * service.
	 */
	private final MirrorMazeServiceAsync mirrorMazeService = GWT
			.create(MirrorMazeService.class);

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {

		Layout masterLayout = new VLayout();
		masterLayout.setWidth100();
		masterLayout.setHeight100();

		DynamicForm s3bucket = new DynamicForm();
		
		final TextItem s3bucketName = new TextItem("s3bucketName", "S3 Bucket Name");
		s3bucketName.addKeyPressHandler(new KeyPressHandler() {
			
			@Override
			public void onKeyPress(KeyPressEvent event) {
				if(event.getKeyName().equals("Enter"))
					mirrorMazeService.importJSONFromS3(s3bucketName.getDisplayValue(), new AsyncCallback<Boolean>() {
						
						@Override
						public void onSuccess(Boolean result) {
							System.out.println("import success.");
						}
						
						@Override
						public void onFailure(Throwable caught) {
							System.out.println("import failed.");
						}
					});
			}
		});
		s3bucket.setItems(s3bucketName);
		masterLayout.addMember(s3bucket);
		
		//masterLayout.addMember(new UploadTestsView().getContent());
		
		masterLayout.draw();
	}
}

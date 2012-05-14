package edu.kit.aifb.mirrormaze.client;

import gwtupload.client.IUploader;
import gwtupload.client.MultiUploader;
import gwtupload.client.IUploadStatus.Status;
import gwtupload.client.IUploader.OnFinishUploaderHandler;
import gwtupload.client.IUploader.UploadedInfo;

import org.swfupload.client.SWFUpload;
import org.swfupload.client.UploadBuilder;
import org.swfupload.client.SWFUpload.ButtonAction;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.RootPanel;
import com.smartgwt.client.types.Encoding;
import com.smartgwt.client.types.FormMethod;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.UploadItem;
import com.smartgwt.client.widgets.layout.Layout;
import com.smartgwt.client.widgets.layout.VLayout;

public class UploadTestsView {
	
	private Layout getLayout() {
		return new VLayout();
	}

	public Layout getContent() {
		
		final DynamicForm uploadForm = new DynamicForm();
		uploadForm.setAction(GWT.getModuleBaseURL() + "mirrormaze/import");
		uploadForm.setMethod(FormMethod.POST);
		uploadForm.setEncoding(Encoding.MULTIPART);
		final UploadItem fileItem = new UploadItem();
		fileItem.setTitle("Template");
		fileItem.setWidth(300);
		fileItem.setAttribute("multiple", "multiple");
		uploadForm.setItems(fileItem);

		IButton uploadButton = new IButton("Upload");
		uploadButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent e) {
				Object obj = fileItem.getDisplayValue();
				if (obj != null) {
					uploadForm.submitForm();
				} else
					SC.say("Please select JSON files.");
			}
		});

		getLayout().addMember(uploadForm);
		getLayout().addMember(uploadButton);

		MultiUploader JSONUploader = new MultiUploader();
		JSONUploader.getElement().getElementsByTagName("input").getItem(0)
				.setAttribute("multiple", "multiple");
		JSONUploader.addOnFinishUploadHandler(new OnFinishUploaderHandler() {

			@Override
			public void onFinish(IUploader uploader) {
				if (uploader.getStatus() == Status.SUCCESS) {

					// The server sends useful information to the client by
					// default
					UploadedInfo info = uploader.getServerInfo();
					System.out.println("File name " + info.name);
					System.out.println("File content-type " + info.ctype);
					System.out.println("File size " + info.size);

					// You can send any customized message and parse it
					System.out.println("Server message " + info.message);
				}

			}
		});

		getLayout().addMember(JSONUploader);

		UploadBuilder builder = new UploadBuilder();

		// Configure which file types may be selected
		builder.setFileTypes("*.asf;*.wma;*.wmv;*.avi;*.flv;*.swf;*.mpg;*.mpeg;*.mp4;*.mov;*.m4v;*.aac;*.mp3;*.wav;*.png;*.jpg;*.jpeg;*.gif");
		builder.setFileTypesDescription("Images, Video & Sound");

		// Configure the button to display
		builder.setButtonPlaceholderID("swfupload");
		builder.setButtonImageURL("XPButtonUploadText_61x22.png");
		builder.setButtonWidth(61);
		builder.setButtonHeight(22);
		builder.setButtonText("<span class=\"label\">Browse</span>");
		builder.setButtonTextStyle(".label { color: #000000; font-family: sans font-size: 16pt; }");
		builder.setButtonTextLeftPadding(7);
		builder.setButtonTextTopPadding(4);

		// Use ButtonAction.SELECT_FILE to only allow selection of a single file
		builder.setButtonAction(ButtonAction.SELECT_FILES);

		// The placeholder to be replaced by the swfupload control
		// This could also be provided statically by your host HTML page
		Element placeHolder = DOM.createDiv();
		placeHolder.setId("swfupload");
		RootPanel.get().getElement().appendChild(placeHolder);

		// The button to start the transfer
		IButton uploadSWFButton = new IButton("Upload");
		getLayout().addMember(uploadSWFButton);

		final SWFUpload upload = builder.build();

		uploadSWFButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				upload.startUpload();
			}
		});
		
		
		return null;
	}

	public void refresh() {
		
	}

}

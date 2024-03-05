package it.tdlight.telegrambackup;

import it.tdlight.jni.TdApi.Message;
import it.tdlight.jni.TdApi.MessageAnimation;
import it.tdlight.jni.TdApi.MessageAudio;
import it.tdlight.jni.TdApi.MessageChatChangePhoto;
import it.tdlight.jni.TdApi.MessageChatChangeTitle;
import it.tdlight.jni.TdApi.MessageChatUpgradeFrom;
import it.tdlight.jni.TdApi.MessageChatUpgradeTo;
import it.tdlight.jni.TdApi.MessageContent;
import it.tdlight.jni.TdApi.MessageDocument;
import it.tdlight.jni.TdApi.MessagePhoto;
import it.tdlight.jni.TdApi.MessagePoll;
import it.tdlight.jni.TdApi.MessageSticker;
import it.tdlight.jni.TdApi.MessageText;
import it.tdlight.jni.TdApi.MessageVideo;
import it.tdlight.jni.TdApi.MessageVideoNote;
import it.tdlight.jni.TdApi.MessageVoiceNote;

public class MessageHandler {
	public static void handleMessage(Message message) {
		//TODO
		MessageContent content = message.content;
		switch(content) {
			case MessageAnimation msg -> {
				
			}
			case MessageAudio msg -> {
							
			}
			case MessageChatChangePhoto msg -> {
				
			}
			case MessageChatChangeTitle msg -> {
				
			}
			case MessageChatUpgradeFrom msg -> {
				
			}
			case MessageChatUpgradeTo msg -> {
				
			}
			case MessageDocument msg -> {
				
			}
			case MessagePhoto msg -> {
				
			}
			case MessagePoll msg -> {
				
			}
			case MessageSticker msg -> {
				
			}
			case MessageVideo msg -> {
				
			}
			case MessageVideoNote msg -> {
				
			}
			case MessageVoiceNote msg -> {
				
			}
			case MessageText msg -> {
				
				//msg.c
			}
			default -> throw new IllegalArgumentException("Unexpected value: " + content);
		}
	}
	
	public static void archiveFile() {
		//TODO
	}
}

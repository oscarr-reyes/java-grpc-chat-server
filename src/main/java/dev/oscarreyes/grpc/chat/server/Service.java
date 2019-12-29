package dev.oscarreyes.grpc.chat.server;

import com.google.protobuf.Timestamp;
import dev.oscarreyes.grpc.ChatMessage;
import dev.oscarreyes.grpc.ChatServiceGrpc;
import dev.oscarreyes.grpc.ResponseMessage;
import io.grpc.stub.StreamObserver;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class Service extends ChatServiceGrpc.ChatServiceImplBase {
	private static Set<StreamObserver<ResponseMessage>> observers = ConcurrentHashMap.newKeySet();

	@Override
	public StreamObserver<ChatMessage> chat(StreamObserver<ResponseMessage> responseObserver) {
		observers.add(responseObserver);

		return new StreamObserver<ChatMessage>() {
			@Override
			public void onNext(ChatMessage value) {
				ResponseMessage responseMessage = ResponseMessage.newBuilder()
					.setMessage(value)
					.setTimestamp(Timestamp.newBuilder()
						.setSeconds(System.currentTimeMillis() / 1000)
					)
					.build();

				for (StreamObserver<ResponseMessage> observer : observers) {
					observer.onNext(responseMessage);
				}
			}

			@Override
			public void onError(Throwable t) {

			}

			@Override
			public void onCompleted() {
				observers.remove(responseObserver);
			}
		};
	}
}

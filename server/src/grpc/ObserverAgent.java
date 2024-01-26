package grpc;

import informationcontainer.RoundResult;
import io.grpc.stub.ServerCallStreamObserver;
import io.grpc.stub.StreamObserver;
import protoc.EnumProto.GrpcFlag;
import protoc.ServiceProto.SpectateRequest;
import protoc.ServiceProto.SpectatorGameState;
import setting.GameSetting;
import struct.AudioData;
import struct.FrameData;
import struct.GameData;
import struct.ScreenData;
import util.GrpcUtil;

public class ObserverAgent {
	
	private int interval;
	private boolean frameDataFlag;
	private boolean screenDataFlag;
	private boolean audioDataFlag;
	
	private boolean cancelled;
	private StreamObserver<SpectatorGameState> responseObserver;
	
	public ObserverAgent() {
		this.cancelled = true;
	}
	
	public void register(SpectateRequest request, StreamObserver<SpectatorGameState> responseObserver) {
		if (!this.isCancelled()) {
			this.notifyOnCompleted();
			this.cancel();
		}
		
		((ServerCallStreamObserver<SpectatorGameState>) responseObserver).setOnCancelHandler(new Runnable() {
			@Override
			public void run() {
				ObserverAgent.this.cancel();
			}
		});
		
		this.interval = request.getInterval();
		this.frameDataFlag = request.getFrameDataFlag();
		this.screenDataFlag = request.getScreenDataFlag();
		this.audioDataFlag = request.getAudioDataFlag();
		
		this.cancelled = false;
		this.responseObserver = responseObserver;
	}
	
	public boolean isCancelled() {
		return this.cancelled;
	}
	
	public void cancel() {
		this.responseObserver = null;
		this.cancelled = true;
	}
	
	public void notifyOnCompleted() {
		if (!this.isCancelled()) {
			this.responseObserver.onCompleted();
		}
	}
	
	public void onInitialize(GameData gameData) {
		if (this.isCancelled()) {
			return;
		}
		
		SpectatorGameState response = SpectatorGameState.newBuilder()
  				.setStateFlag(GrpcFlag.INITIALIZE)
  				.setGameData(GrpcUtil.convertGameData(gameData))
  				.build();
		this.onNext(response);
	}
	
	public void onGameUpdate(FrameData frameData, AudioData audioData, ScreenData screenData) {
		if (this.isCancelled()) {
			return;
		}
		
		if (frameData.getFramesNumber() % this.interval == 0) {
			SpectatorGameState.Builder response = SpectatorGameState.newBuilder()
					.setStateFlag(GrpcFlag.PROCESSING);
			
			if (this.frameDataFlag) {
				response.setFrameData(GrpcUtil.convertFrameData(frameData));
			}
			
			if (this.screenDataFlag) {
				response.setScreenData(GrpcUtil.convertScreenData(screenData));
			}
			
			if (this.audioDataFlag) {
				response.setAudioData(GrpcUtil.convertAudioData(audioData));
			}
			
			this.onNext(response.build());
		}
	}
	
	public void onRoundEnd(RoundResult roundResult) {
		if (this.isCancelled()) {
			return;
		}
		
		boolean isGameEnd = roundResult.getRound() >= GameSetting.ROUND_MAX;
		SpectatorGameState response = SpectatorGameState.newBuilder()
				.setStateFlag(isGameEnd ? GrpcFlag.GAME_END : GrpcFlag.ROUND_END)
				.setRoundResult(GrpcUtil.convertRoundResult(roundResult))
  				.build();
		this.onNext(response);
	}
	
	public void onNext(SpectatorGameState state) {
		if (!this.isCancelled()) {
			this.responseObserver.onNext(state);
		}
	}

}

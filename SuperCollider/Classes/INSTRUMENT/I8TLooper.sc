I8TLooper : Instrument
{


		var <buffers;
		var maxDuration;
		var numChannels;

		var <recSynth;
		var <playSynths;
		var <durations;
		var lastDuration;

		*new{|name_,synthdef_,mode_=nil|
			^super.new.init(name_,this.graph,synthdef_,mode_);
		}

		init{|name_,graph_,synthdef_,mode_=nil|

			buffers = IdentityDictionary.new;
			playSynths = IdentityDictionary.new;
			durations = IdentityDictionary.new;

			maxDuration = 60;
			numChannels = 1;

			super.init(name_,graph_);

		}



		rec {|layer|

			if( layer.notNil, {
				// if buffer for layer not allocated,
				if( buffers[ layer ].isKindOf(Buffer) == false ) {
					buffers[ layer ] = Buffer.alloc( Server.local, Server.local.sampleRate * maxDuration, 1,
						{|buffer|
							recSynth = Synth(\loopWrite, [
								\inBus, 0,
								\buffer, buffer
							]);
							lastDuration = TempoClock.default.beats;
						});
				};




			}, {

				// if buffer for layer not allocated,
				if( buffers[ buffers.size ].isKindOf(Buffer) == false ) {
					buffers[ buffers.size ] = Buffer.alloc( Server.local, Server.local.sampleRate * maxDuration, 1, {|buffer|
						recSynth = Synth(\loopWrite, [
							\inBus, 0,
							\buffer, buffer
						]);
						lastDuration = TempoClock.default.beats;
					});
				};



			});
		}

		delete {|layer|
			// if no layers selected
			// delete all available layers

				// if layer exists
				// delete it
		}


		stopRecording {

			var recDuration;

			if( recSynth.isKindOf(Synth)) {
				recSynth.free;
				recDuration = TempoClock.default.beats - lastDuration;
				// recDuration = recDuration / TempoClock.default.tempo;
				durations[buffers.size-1]=recDuration;
				recSynth = nil;
				["Duration:", recDuration,buffers.size-1,TempoClock.default.beats, lastDuration;].postln;
			};

		}

		play {|layer|

			this.stopRecording();

			// if no layers selected
			if( layer.isNil, {
				// play all available layers:
				// first stop all
				playSynths.collect({|synth|
					if( synth.isKindOf(Synth) ) {
						synth.release;
					};
				});
				// then create new synths:
				buffers.collect({|buffer,key|
					if( buffer.isKindOf(Buffer) ) {
						playSynths[key]=Synth(\loopRead,[
							\buffer, buffer,
							\duration, durations[key]
						]);
						["playdur",key,durations,durations[key]].postln;
					};
				});
			}, {

				// if layer exists
				if( buffers[layer].isKindOf(Buffer) ) {
					// play it:
					// first stop it if running

					if( playSynths[layer].isKindOf(Synth)) {
						playSynths[layer].release;
					};
					// then create new synth:

					playSynths[layer]=Synth(\loopRead,[
						\buffer, buffers[layer],
						\duration, durations[layer]
					]);
					["playdur",durations[layer]].postln;
				};

			});

		}

		stop {|layer|


			this.stopRecording();


			// if no layers selected
			if( layer.isNil, {
				// stop all available layers
				buffers.collect({|buffer,key|
					if( buffer.isKindOf(Buffer) ) {
						playSynths[key].release;
					};
				});
			}, {

				// if layer exists
				if( buffers[layer].isKindOf(Buffer) ) {
					// stop it
					playSynths[layer].release;
				};

			});

		}

		seek {|layer|

			// if no layers selected
			// seek all available layers

				// if layer exists
				// seek it

		}


}
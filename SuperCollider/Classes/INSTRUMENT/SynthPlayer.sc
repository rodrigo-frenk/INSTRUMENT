SynthPlayer : Instrument
{
	var group;
	var groupID;

	var amp;

	var nodeID;
	var nodeIDs;
	var <synthdef;
	var <>mode;

	var synth_parameters;
	var fx_parameters;

	var <fxSynth;
	var <fx;
	var fxBus;

	var pressedKeys;

	var currentPressedKey;
	var lastPressedKey;

	var autostart;


	*new{|synthdef_,mode_,name_,autostart,fx|
		^super.new.init(this.graph,synthdef_,mode_,name_,autostart,fx);
	}

	init{|graph_,synthdef_,mode_,name_,autostart_,fx|

		nodeIDs=IdentityDictionary.new;

		group = Group.new;
		group.register;
		groupID = group.nodeID;


		if( mode_.notNil, {
			mode = mode_;
		}, {
			mode = \poly;
		});

		if( synthdef_.notNil, {
			// [name_,synthdef_].postln;

			if(synthdef_.isKindOf(Symbol), {
				synthdef = synthdef_;
			},{
				synthdef = \test;
			});

			// this.createSynth();
			fxSynth = nil;
			fxBus = Bus.audio(Server.local,2);
			synth_parameters = IdentityDictionary.new;
			fx_parameters = IdentityDictionary.new;
			super.init(graph_,name_);

		});


		pressedKeys = IdentityDictionary.new;

		if( autostart_ == true ) {
			this.fx_( fx );
			this.createSynth();
			autostart=autostart_;
		};
	}

	synthdef_{|synthdef_|

		synthdef = synthdef_;
		// if( synth_parameters.isKindOf(IdentityDictionary) == false, {
		// 	synth_parameters=IdentityDictionary.new;
		// });

		this.createSynth();

		^synthdef

	}

	createSynth{|parameters|

		if( synthdef != \r, {

			var s = Server.local;

			// if( synths.isKindOf(List), {
				// clean dead synths' id
					var removeKey;
					synths.collect({|synth_,key|
						if( synth_.isPlaying == false, {
							nodeIDs[synth_.nodeID]=false;
							nodeID=synth_.nodeID;
							removeKey = key;
						});
					});
					if( removeKey.notNil,{
						synths.removeAt( removeKey );
					}, {
						nodeID = nil;
					});
				// });

				if( nodeID.isNil, {

					var idIndex = nil;

					nodeIDs.collect({|id_set,key|
						if( id_set==false, { idIndex=key });
					});

					if(idIndex.notNil, {
						nodeID = idIndex;
					}, {
						nodeID = s.nextNodeID;
					});

				});

			if( fxSynth.isKindOf(Synth), {

				synth = Synth.before( fxSynth, synthdef.asSymbol, [\out,fxBus]++parameters );
				synth.register;
			}, {
				var initNodeID = nodeID;
				// s.sendBundle(0,["/n_free",nodeID]);
				// if( synth.isPlaying, {
					initNodeID = s.nextNodeID;
					// synth.release;
				// });

				synth = Synth.basicNew( synthdef.asSymbol, s, initNodeID );
				synth.register;
				synths.add(synth);
				nodeIDs[initNodeID]=true;
				// [[\out,fxBus]++parameters].postln;
				// s.sendBundle(0,synth.addToHeadMsg(group, [\freq,300]));
				s.sendBundle(0,synth.addToHeadMsg(group, parameters));

			});
			nodeID = synth.nodeID;

		});


	}

	parameters_array{|array|
		var parameters_array = List.new;

		array.keysValuesDo({|key,value|
			parameters_array.add(key.asSymbol);
			parameters_array.add(value);
		})

		^parameters_array
	}

	trigger {|parameter,value|
		[parameter,value].postln;
		if( value.notNil ) {


			if( value.isKindOf(Event) == false, {
				value = ( val: value, amplitude: 0.5 );
			});

			switch( parameter,

				\synthdef, {
					synthdef = value.val;
					// synth_parameters = IdentityDictionary.new;
				},
				\octave, { octave = value.val },
				\fx, {
					[parameter,value].postln;
					this.fx_( value.val );

				},
				\setFx, {

					value.keysValuesDo({|k,v|
						[k,v].postln;
						fx_parameters[k]=v;
						fxSynth.set(k,v);
					});
				},
				\note, {
					// if is Event, get params
					var event = value;
					var note = event.val.asFloat;
					var amp = event.amplitude;
					var use_synth_parameters;

					if( event.amplitude.isNil ) {
						amp = 0.5;
					};

					use_synth_parameters = synth_parameters;

					if( ((synth_parameters.notNil) && (synth_parameters[\amp].notNil)), {
						var computed_params;
						amp = amp * synth_parameters[\amp];

						computed_params = synth_parameters.copy;
						computed_params.removeAt(\amp);
						use_synth_parameters = computed_params;
					});

					if( amp.asFloat > 0, {

						switch(mode,

							\poly, {

								this.createSynth([
									\t_trig,1,
									\freq,((octave*12)+note).midicps,
									\note,(octave*12)+note,
									\amp, amp
									]++this.parameters_array(use_synth_parameters)
								);

							},

							\mono, {

								if( synth.notNil, {

									if( synth.isPlaying == false, {
										synth = nil;
									});
								});

								if( synth.isNil, {

									this.createSynth([
										\t_trig,1,
										\freq,((octave*12)+note).midicps,
										\note,(octave*12)+note,
										\amp, amp
										]++this.parameters_array(use_synth_parameters)
									);

								}, {

									pressedKeys[note] = true;

									synth.set(\amp,amp);
									synth.set(\gate,1);
									synth.set(\freq,note.midicps);

								});

							}
						);


					}, { // note off


						switch( mode,

							\mono, {

								pressedKeys.removeAt(note);

								if(pressedKeys.size<=0, {

									synth.set(\gate,0);
									pressedKeys = IdentityDictionary.new;

								});

							}
						);

					});




				},
				\trigger, {
					var floatValue = value.val.asFloat;
					if( floatValue.asFloat > 0 ) {

						var amp = floatValue;
						var use_synth_parameters;
						use_synth_parameters = synth_parameters;
						// ["should create synth", floatValue.isKindOf(String),floatValue>0].postln;


						if( ((synth_parameters.notNil) && (synth_parameters[\amp].notNil)), {
							var computed_params;
							amp = amp * synth_parameters[\amp];
							computed_params = synth_parameters.copy;
							computed_params.removeAt(\amp);
							use_synth_parameters = computed_params;
						});
						this.createSynth([\t_trig,1,\amp,amp]++this.parameters_array(use_synth_parameters));
					}
				},
				// \t_trig, { this.createSynth([\t_trig,1,\note,(octave*12)+value.val]); },
				\chord, {
					// ["chord",value].postln;
					// proxy.setn(\notes,(octave*12)+value,\freqs,((octave*12)+value).midicps,\t_trig,1);
				},
				{ // default:
					synth_parameters[parameter.asSymbol]=value.val;
					if( value.val.isNil || value.val == 0, {}, { synth.set(parameter.asSymbol,value.val) });
				},


			);


		}

	}

	fx_ {|synthdef_|

		var fx;


		if( synthdef_.notNil,{
			if( fxSynth.notNil, {
				fxSynth.free;
				// fxSynth = Synth.replace(fxSynth,synthdef_);
			}, {
				// fxSynth = Synth.new(synthdef_);
			});

			fxSynth = Synth.new(synthdef_.asSymbol,[\inBus,fxBus]++this.parameters_array(fx_parameters));

			if( autostart == true ) {

				this.createSynth();

				if( synth_parameters[\inBus].notNil ) {
					this.set(\inBus,synth_parameters[\inBus]);
				};

			};

		}, {
			"clear currentFX".postln;
			fxSynth.free;
			fxSynth = nil;
		});

		^fxSynth;

	}



	fxSynth_{|synthdef_|
		this.fx(synthdef_);
	}

	setFx{|parameter,value|
		fx_parameters[parameter] = value;
		fxSynth.set(parameter,value);
	}

	set {|parameter,value|

		if( parameter == \note, {
			this.trigger( parameter, value );
		}, {
			synth_parameters[parameter] = value;
		});

		synth.set( parameter, value );
	}

	amp_ {|value|

		synth_parameters[\amp] = value;
		synth.set( \amp, value );

	}

	amp {|value|
		if( value.notNil ) {
			synth_parameters[\amp] = value;
			synth.set( \amp, value );
		};
		^amp;
	}

	stop {
		if( mode == \mono, {
			synth.release;
		});
		super.stop();
	}

}

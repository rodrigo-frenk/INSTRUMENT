InstrumentGroup : Event {

	var <amp;
	var <clock;
	var <baseClock;
	var <fx;
	var <>name;

	play {
		this.collect({|item|
			if( (item.isKindOf(Instrument)) || (item.isKindOf(InstrumentGroup))) {
				item.play;
			};
		});
	}
	stop {
		this.collect({|item|
			if( (item.isKindOf(Instrument)) || (item.isKindOf(InstrumentGroup))) {
				item.stop;
			};
		});
	}
	go {|time|
		this.collect({|item|
			if( (item.isKindOf(Instrument)) || (item.isKindOf(InstrumentGroup))) {
				item.go(time);
			};
		});
	}
	set {|parameter,value_|

		this.collect({|item|
			if( (item.isKindOf(Instrument)) || (item.isKindOf(InstrumentGroup))) {
				item.set(parameter,value_);
			};
		});

	}

	amp_ {|value_|

		this.collect({|item|
			if( (item.isKindOf(Instrument)) || (item.isKindOf(InstrumentGroup))) {
				item.amp = value_;
			};
		});
		amp = value_;
	}


	setClock {|speed_|
		if( speed_.isKindOf(Number) ) {
			if( speed_>0 && speed_ < 256 ) {
				var newClock = speed_;
				if( baseClock.notNil) {
					newClock = speed_ * baseClock;
				};
				this.collect({|item|

					if( (item.isKindOf(Instrument)) || (item.isKindOf(InstrumentGroup))) {
						item.setClock(speed_);
					};

				});
			}
		}
	}

	clock_ {|speed_|

		if( speed_.isKindOf(Number) ) {
			if( speed_>0 && speed_ < 256 ) {
				baseClock = speed_;
				clock = speed_;
				this.collect({|item|
					if( (item.isKindOf(Instrument)) || (item.isKindOf(InstrumentGroup))) {
						item.setClock(speed_);
					};
				});
			}

		}



	}

	// speed {|value_|
	//
	//
	// 	^speed
	//
	// }

	fx_ {|value_|
		this.collect({|item|
			if( (item.isKindOf(Instrument)) || (item.isKindOf(InstrumentGroup))) {
				item.fx=value_;
			};
		});
		fx = value_;
	}


	fxSet{|parameter_,value_|
		this.collect({|item|
			if( (item.isKindOf(Instrument)) || (item.isKindOf(InstrumentGroup))) {
				item.fxSet(parameter_,value_);
			};
		});
	}

	seq {|parameter_,value_|
		this.collect({|item|
			if( (item.isKindOf(Instrument)) || (item.isKindOf(InstrumentGroup))) {
				item.seq(parameter_,value_);
			};
		});
	}

	rm {|parameter_,value_|
		this.collect({|item|
			if( (item.isKindOf(Instrument)) || (item.isKindOf(InstrumentGroup))) {
				item.rm(parameter_,value_);
			};
		});
	}


}

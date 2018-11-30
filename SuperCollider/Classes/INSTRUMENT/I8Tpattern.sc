I8Tpattern
{

	var <>parameters;
	var <>pattern;

	var <hasDurations;

	*new{|pattern_,parameters_|
		^super.new.init(pattern_,parameters_);
	}
	init{|pattern_,parameters_|

		if( pattern_.isString, {


			var events = this.parseEventString(pattern_);
			// var values = List.new;
			var patternEvents = List.new;


			var amplitudes = events.collect{|e|
				if( e.amplitude.isNil, { 0.5; }, { e.amplitude; });
			};

			events.collect({arg event; event.postln; });

			hasDurations = false;


			events.collect({|e,i|

				var newPatternEvent = ();

				if( e.hasDurations == true, {
					hasDurations = true;
				});

				newPatternEvent.val = e.val;
				newPatternEvent.duration = e.duration;

				if( e.val != \r, {
					newPatternEvent.amplitude = amplitudes[i];
				});


				if(e.repetitions.notNil, {
					e.repetitions.do{
						// values.add(e.val);
						patternEvents.add(newPatternEvent);
					}
				}, {
					// values.add(e.val);
					patternEvents.add(newPatternEvent);
				});

			});

			// pattern = values.asArray;
			pattern = patternEvents.asArray;

		}, {

			if( pattern_.isArray, {

				pattern = pattern_.collect({|patternValue|
					if( patternValue.isKindOf(Event), {
						patternValue;
					}, {
						( val: patternValue );
					});
				});

			}, {

				["!!Pattern not recognized",pattern].postln;

			});

		});

		parameters = parameters_;
	}




    parseEventString {|input|

        var spaces;
        var currentGroup;
        var events;

        events = List.new;

        // spaces = input.findAll(" ");

        input.size.do({|i|
            var char = input[i];

	        if( currentGroup == nil, {

// detect if time fraction after rests

                if( char.asString.compare(" ") == 0, {
"add space rest".postln;
					events.add( ( val: \r ) );

                },
                {

				    // not a space
                    currentGroup = ();

                    // currentGroup.index = i;
                    currentGroup.chars = List[char];

					if( i == (input.size - 1), {
"close end group".postln;
						events.add( this.closeEventGroup(currentGroup) );
						currentGroup = nil;
					});

                });
            }, {

                // if currentGroup not Nil,

				// if last item
                if( ( i == (input.size - 1)), {

					if( char.asString.compare(" ") != 0,  {
						currentGroup.chars.add( char );
					});

					["close group end char", cveurrentGroup].postln;

					events.add( this.closeEventGroup(currentGroup) );
					currentGroup = nil;

					if( char.asString.compare(" ") == 0,  {

						"add space rest when last".postln;

						events.add( ( val: \r ) );
                    });


                }, {
					// not last item

					if(char.asString.compare(" ") == 0, {


						if( input[i+1].asString.compare(" ") == 0, {


							var hasRestDuration;
							var restDurationIndex;
							var testStringLength;




							restDurationIndex = input.find(" :");



							if( restDurationIndex.notNil, {
								var testString = "";

								restDurationIndex = restDurationIndex + 1;

								testStringLength = restDurationIndex - i;

								testStringLength.do{|a|

									testString = testString ++ " ";

								};

								hasRestDuration = testString.asString.matchRegexp( input, i, restDurationIndex );
["restDurationIndex",restDurationIndex].postln;
								if( hasRestDuration, {

									var nextSpace = input.find(" ", offset: restDurationIndex);

									var upperLimit = nextSpace;

									var totalChars;

									var durationValue = "";


									if( nextSpace.isNil ) {
										upperLimit = input.size - 1;
									};

									totalChars = upperLimit - restDurationIndex;


									totalChars.do{|i|
										durationValue = durationValue ++ input[restDurationIndex+1+i];
									};

									durationValue = durationValue.asFloat;

									["durationValue",durationValue].postln;

									"add space with duration".postln;

							 		events.add( ( val: \r, duration: durationValue ) );

								}, {
// when ': ' not immediately after current index char group of spaces


"add rest".postln;

									// events.add( ( val: \r ) );

									events.add( this.closeEventGroup(currentGroup) );
									currentGroup = nil;


								});




							}, {

								var areAllNextCharsSpaces = true;


								(input.size - (i+1)).do{|j|
									if( input[ (input.size-1) - j].asString.compare(" ") != 0, {
										areAllNextCharsSpaces = false;
									})
								};


								if( areAllNextCharsSpaces, {

									"all next spaces".postln;

									events.add( ( val: \r ) );
								})

							});




						})

					}, {

						// not a space
						currentGroup.chars.add( char );

					});
                });

            })

        });
        ^events;

    }

	closeEventGroup{|group|

		var str = "";
		var splitStr;
		var newGroup = ();


		group.chars.collect({|c| str = str ++ c });


		if( str.find(":").notNil, {

			splitStr = str.split($:);

			newGroup.val = splitStr[0].asFloat;

			if( splitStr[1].find("/").notNil, {

				var fraction1, fraction2;
				fraction1 = splitStr[1].split($/)[0];
				fraction2 = splitStr[1].split($/)[1];

				newGroup.duration = fraction1.asFloat / fraction2.asFloat;

			}, {

				newGroup.duration = splitStr[1].asFloat;
				newGroup.amplitude = this.getAmplitude( splitStr[1] );

			});

			if( this.getRepetitions( splitStr[0] ) > 1, {
				newGroup.repetitions = this.getRepetitions( splitStr[0] );
				newGroup.amplitude = this.getAmplitude( splitStr[0] );
			});

			newGroup.hasDurations = true;
		}, {

			newGroup.val = str.asFloat;

			if( this.getRepetitions( str ) > 1, {
				newGroup.repetitions = this.getRepetitions( str );
			});

			newGroup.amplitude = this.getAmplitude( str );

		});

		"return new group".postln;

		^newGroup;

	}

	getAmplitude{|string|

		var amplitude = 0.5;
		var factor = 0.1;

		if( string.find("*").notNil, {
			amplitude = this.getOperatorParameter(string, "*")
			},
		{

			if( string.find("p").notNil, {

				amplitude = amplitude - (factor * this.getOperatorValue(string, "p"));

			});
			if( string.find("f").notNil, {

				amplitude = amplitude + (factor * this.getOperatorValue(string, "f"));

			});
		});

		^amplitude.asFloat;


	}

	getRepetitions{|string|

		var repetitions = 0;

		if( string.find("x").notNil, {
			repetitions = this.getOperatorValue(string,"x");
		});

		^repetitions.asInteger;

	}

	getOperatorValue {|string,char|
		var operatorValue = 0;

		if(
			(
				string.findBackwards(char) == (string.size - 1)
				||
				this.isBeforeOtherOperator(string,string.findBackwards(char))
			)
		, {
			operatorValue = this.getOperatorRepetitions(string, char);
		}, {
			operatorValue = this.getOperatorParameter(string,char);
		});

		^operatorValue;

	}

	getOperatorRepetitions {|string,char|

		var indexes = string.findAll( char );

		var operatorValue = 0;

		if(this.areIndexesSequential(indexes),{
			operatorValue = indexes.size;
		});

		^operatorValue
	}

	getOperatorParameter {|string,char|

		var indexes = string.findAll( char );

		var operatorValue = 0;

		var operatorValueStr = "";

		((string.size-1) - indexes.maxItem).do{|index|
			operatorValueStr = operatorValueStr ++ string[(string.size-1)-index];
		};

		operatorValue = operatorValueStr.reverse.asInteger;

		^operatorValue

	}



	areIndexesSequential {|indexes|

		var sequential=true;

		var lastIndex = indexes[0];

		indexes.collect({|i|
			if( (i.asInteger - lastIndex.asInteger) > 1, {
				sequential=false;
			}, {
				lastIndex = i;
			});
		});

		if( sequential == false, {
			"invalid Pattern: x's must be sequential".postln;
		});

		^sequential;

	}

	isBeforeOtherOperator {|string,index|

		var isBefore = false;

		var operators = ["p","f","x",":","*"];

		operators.collect({|o|
			if( string[ index + 1 ].asString.compare(o) == 0, {
				isBefore = true;
			});
		});

		^isBefore;
	}

}

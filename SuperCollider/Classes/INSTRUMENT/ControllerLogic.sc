ControllerLogic {

	// State for storing values and callbacks
	var state;
	var inputs;
	var outputs;
	var >listener;

	*new {
		^super.new.init();
	}

	init {

		state = ();

		state.values = Array.fill(72,nil);
		state.callbacks = Array.fill(72,nil);

	}


	updateViews {

	}



	addInput {|input|
		inputs.push( input );
	}

	removeInput {|input|
		inputs.removeAt( inputs.indexOf(input) );
	}


	addOutput {|input|
		inputs.push( input );
	}

	removeOutput {|output|
		outputs.removeAt( outputs.indexOf(output) );
	}


	notify{
		var event;
		event = ();
		event.name = "Test Event";
		listener.notify( event );
	}


}

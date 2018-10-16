EventListener
{

	var controller;
	var source;

	*new {|controller_,source_|
		^super.new.init(controller_,source_);
	}

	init {|controller_,source_|
		controller = controller_;
		source = source_;
		source.listener = this;
	}



	notify {|event|
		controller.notify(source.key, event);
	}

}

MIDIDevice {

    var <controllers;
    var <output;
    var <outputMap;
    var <groups;
    var device;
    var spec;
    var <>name;
    var <slug;
    var >midi;
    var <protocol;

    *new {|midiManager,device, spec_|
        ^super.new.init(midiManager,device, spec_);
    }

    init {|midiManager,device_, spec_|

        midi = midiManager;
        device = device_;
        protocol = "midi";
        name = device.device;
        spec=spec_;

        slug = name.replace(" ","_").toLower.asSymbol;
        groups = ();

		controllers = ();


        if( spec.notNil, {

            // if( spec.outputs.isInteger, {
            var port;

            MIDIClient.destinations.collect({|destination, index|

                if(spec.name == destination.device, {
                    port = index;
                });

            });

            if( port.notNil, {
                ["port",port].postln;
                output = MIDIOut.new( port );
                output.connect( port );

                outputMap = spec.map;

                outputMap.collect({|outputMapping|
                    outputMapping.type=spec.outputType;
                });


                ["outputMap",outputMap].postln;

            });

        });



	}

    addControllerGroup {|type, name|
        groups[name] = ControllerGroup(type,name,this);
        ^groups[name];
    }

    removeControllerGroup {|name|
        if( groups[name].notNil, {
            groups.removeAt(name);
        });
    }

    send {|key,value|

        ["send:", key, "to", outputMap[key].ctlNum, value].postln;

        switch( outputMap[key].type,
            \note, {

                output.noteOn(0,outputMap[key].ctlNum,value)
                
            }
        );

    }

	set {|source, param1, param2|

        var key = source.key;

        ^midi.set(source,param1,param2);

	}


}

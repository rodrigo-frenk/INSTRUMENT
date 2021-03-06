SynthDef(\reverbLPF, { arg inBus=0, cutoff=1500,q=0.075, room=0.4,damp=0.6, wet=0.75;
    Out.ar(
        [0,1],(

            (In.ar(inBus) * (1-wet))+
            (RLPF.ar(
                (FreeVerb.ar(In.ar(inBus),room,damp)*3).tanh, cutoff, q
            )*wet

        )*0.75);
    )
}).store;

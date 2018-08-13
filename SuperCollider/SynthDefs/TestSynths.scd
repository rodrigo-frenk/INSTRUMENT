SynthDef(\test,{|note=60,t_trig=0,decay=0.3,distortion=1.3,cutoff=1500|
	var sig, env;
	sig = SinOsc.ar( note.midicps ) * 0.1;
	env = EnvGen.kr(Env.perc(1/100,decay),t_trig);
	sig = sig * env;
	sig = (sig * distortion).tanh/2;

	sig = RLPF.ar( sig, cutoff, 0.1);

	Out.ar(0, Pan2.ar( sig, 0 ));
}).store;


SynthDef(\bass,{|amp=0.5,note=60,t_trig=0,decay=0.3,distortion=33.3,cutoff=1500,q=0.1|
	var sig, env;
	sig = SinOsc.ar( (note-[24,12]).midicps ) * 0.1;
	env = EnvGen.kr(Env.perc(1/100,decay),t_trig);
	sig = sig * env* amp;
	sig = (sig * distortion).tanh/4;

	sig = RLPF.ar( sig / 2, cutoff, q );

	Out.ar(0, Pan2.ar( sig, 0 ));
}).store;


SynthDef(\bassHard,{|amp=0.5,note=60,t_trig=0,decay=0.4,distortion=12.3,cutoff=1500,q=0.01|
	var sig, env;
	sig = SinOsc.ar( (note-[24,12]).midicps ) * 0.1;
	env = EnvGen.kr(Env.perc(1/100,decay),t_trig);
	sig = sig * env* amp;
	sig = (sig/2 * distortion).tanh/4;

	sig = RLPF.ar( sig, cutoff, q );

	Out.ar(0, Pan2.ar( sig, 0 ));
}).store;


SynthDef(\reverb, { arg rv1 = 0.7, rv2=0.8;
    ReplaceOut.ar(
        1,
        FreeVerb.ar(
            In.ar(0, 1),
            rv1,
            rv2
        )
    )
}).store;

SynthDef(\distortion, { arg gain = 3,cutoff=8000,q=0.1;
    ReplaceOut.ar(
        1,
        (RLPF.ar(In.ar(0, 1),cutoff,q)*gain).tanh/2
    )
}).store;


SynthDef(\delay1, { arg delay = 0.25;
    ReplaceOut.ar(
        1,
        DelayN.ar(
            In.ar(0, 1),
            8,
            delay
        )
    )
}).store;



SynthDef(\delay2, { arg delay=0.25;
	var sig = In.ar(0, 1);
	sig = Mix.arFill(7, { CombC.ar(
		sig, 8, delay, 1
		);
 	});
    ReplaceOut.ar(
        1,
        sig;
    )
}).store;

SynthDef(\delay3, { arg delay = 5;
    ReplaceOut.ar(
        1,
        CombC.ar(
			In.ar(0, 1), 8, LFNoise2.kr(0.3).range(0.1, delay), 1
		);
    )
}).store;



SynthDef(\lpf, { arg cutoff=1500,q=0.1;
    ReplaceOut.ar(
        1,
        RLPF.ar(
			In.ar(0, 1), cutoff, q
		);
    )
}).store;

SynthDef(\hpf, { arg cutoff=70,q=0.3;
    ReplaceOut.ar(
        1,
        RHPF.ar(
			In.ar(0, 1), cutoff, q
		);
    )
}).store;

SynthDef(\resonz, { arg cutoff=1500,q=0.3;
    ReplaceOut.ar(
        1,
        (Resonz.ar(
			In.ar(0, 1), cutoff, q
		)*4).tanh*0.75;
    )
}).store;

SynthDef(\moogFF, { arg cutoff=1500,q=0.5;
    ReplaceOut.ar(
        1,
        (MoogFF.ar(
			In.ar(0, 1), cutoff,3.5+q
		)).tanh*0.75;
    )
}).store;
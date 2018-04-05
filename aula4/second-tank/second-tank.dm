91
SOAR_ID 0
SOAR_ID 1
SOAR_ID 2
SOAR_ID 3
ENUMERATION 4 1 state
ENUMERATION 5 1 nil
ENUMERATION 6 1 tanksoar-default
SOAR_ID 7
SOAR_ID 8
SOAR_ID 9
SOAR_ID 10
SOAR_ID 11
SOAR_ID 12
ENUMERATION 13 1 complete
ENUMERATION 14 1 missile
ENUMERATION 15 4 backward forward left right
ENUMERATION 16 2 off on
INTEGER_RANGE 17 -2147483648 2147483647
ENUMERATION 18 2 left right
SOAR_ID 19
INTEGER_RANGE 20 -2147483648 2147483647
ENUMERATION 21 4 east north south west
ENUMERATION 22 2 no yes
INTEGER_RANGE 23 -2147483648 2147483647
INTEGER_RANGE 24 -2147483648 2147483647
ENUMERATION 25 2 no yes
ENUMERATION 26 2 no yes
ENUMERATION 27 2 no yes
ENUMERATION 28 2 no yes
ENUMERATION 29 2 no yes
INTEGER_RANGE 30 -2147483648 2147483647
ENUMERATION 31 6 black green orange purple red yellow
INTEGER_RANGE 32 -2147483648 2147483647
INTEGER_RANGE 33 -2147483648 2147483647
ENUMERATION 34 2 off on
FLOAT_RANGE 35 -Infinity Infinity
ENUMERATION 36 2 no yes
ENUMERATION 37 2 off on
ENUMERATION 38 5 backward forward left right silent
INTEGER_RANGE 39 -2147483648 2147483647
INTEGER_RANGE 40 -2147483648 2147483647
SOAR_ID 41
SOAR_ID 42
INTEGER_RANGE 43 -2147483648 2147483647
SOAR_ID 44
ENUMERATION 45 1 initialize-tanksoar-default
SOAR_ID 46
SOAR_ID 47
SOAR_ID 48
ENUMERATION 49 1 complete
ENUMERATION 50 1 missile
SOAR_ID 51
ENUMERATION 52 3 center left right
INTEGER_RANGE 53 0 13
SOAR_ID 54
ENUMERATION 55 1 wander
SOAR_ID 56
ENUMERATION 57 1 state
ENUMERATION 58 1 wander
ENUMERATION 59 1 move
SOAR_ID 60
ENUMERATION 61 1 backward
SOAR_ID 62
ENUMERATION 63 1 turn
SOAR_ID 64
SOAR_ID 65
ENUMERATION 66 1 chase
SOAR_ID 67
ENUMERATION 68 1 state
ENUMERATION 69 1 chase
ENUMERATION 70 1 move
SOAR_ID 71
ENUMERATION 72 1 turn
SOAR_ID 73
ENUMERATION 74 1 backward
SOAR_ID 75
SOAR_ID 76
ENUMERATION 77 1 atack
SOAR_ID 78
ENUMERATION 79 1 state
ENUMERATION 80 1 atack
ENUMERATION 81 1 fire-missile
SOAR_ID 82
ENUMERATION 83 1 slide
SOAR_ID 84
ENUMERATION 85 1 move-forward
SOAR_ID 86
ENUMERATION 87 1 turn
SOAR_ID 88
SOAR_ID 89
ENUMERATION 90 1 retreat
108
0 io 1
0 name 6
0 operator 44
0 operator 46
0 operator 54
0 operator 65
0 operator 76
0 operator 89
0 superstate 5
0 top-state 0
0 type 4
1 input-link 2
1 output-link 3
2 blocked 19
2 clock 20
2 direction 21
2 energy 23
2 energyrecharger 22
2 health 24
2 healthrecharger 25
2 incoming 19
2 missiles 30
2 my-color 31
2 radar 41
2 radar-distance 32
2 radar-setting 33
2 radar-status 34
2 random 35
2 resurrect 36
2 rwaves 19
2 shield-status 37
2 smell 42
2 sound 38
2 x 39
2 y 40
3 fire 48
3 move 8
3 radar 9
3 radar-power 10
3 rotate 11
3 shields 12
7 status 13
7 weapon 14
8 direction 15
8 status 13
9 status 13
9 switch 16
10 setting 17
10 status 13
11 direction 18
11 status 13
12 status 13
12 switch 16
19 backward 26
19 forward 27
19 left 28
19 right 29
41 energy 51
41 health 51
41 missiles 51
41 obstacle 51
41 open 51
41 tank 51
42 color 31
42 distance 43
44 name 45
46 actions 47
47 fire 7
48 status 49
48 weapon 50
51 distance 53
51 position 52
54 name 55
56 name 58
56 operator 60
56 operator 62
56 operator 64
56 superstate 0
56 top-state 0
56 type 57
60 name 59
62 name 61
64 name 63
65 name 66
67 name 69
67 operator 71
67 operator 73
67 operator 75
67 superstate 0
67 top-state 0
67 type 68
71 name 70
73 name 72
75 name 74
76 name 77
78 name 80
78 operator 82
78 operator 84
78 operator 86
78 operator 88
78 superstate 0
78 top-state 0
78 type 79
82 name 81
84 name 83
86 name 85
88 name 87
89 name 90

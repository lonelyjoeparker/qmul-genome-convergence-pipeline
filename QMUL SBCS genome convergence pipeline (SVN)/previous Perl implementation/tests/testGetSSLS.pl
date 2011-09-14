#! /usr/bin/perl

##### CLASS DEF ######
#
# 	Test CongruenceTest on a simple dataset.
#
#	Still need to determine which parts of output to parse and return.
#
#
#	EXECUTION:
#
#		1  Read in data
#		2  Read in species tree.
#		3  Pass data and species tree to a CongruenceTest
#		4  CongruenceTest.analyze() and print out results.
#
#
#
##### Date: 5/7/2011 || Joe Parker || QMUL.ac.uk #####


use Carp;
use PhymlSequenceRepresentation;
use NewickTreeRepresentation;
use ExternalFunctions;
use CodemlAnalysis;
use BasemlAnalysis;

$alignment = 'MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
DDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDD
HHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH
AVVVVAAAAAAAAAAAAAAAAAAAAAAAAAAAAVVVVAA
EEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE
EEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE
NTTTTNNNNNNNTTTTTNNNNTTTTTNTNTTNNTTSNNN
EEEEEEEEEEEEEEEEEEEEGEEEEEEEEEEEEEEEEEE
IIIIIIIIIIILIIIIIIIIALIIIIIIIIIIIIIIIII
LLLLLPPLPLLLLLLLLLLLLLLLLLLLLLLLLLLLLLL
EAAAAVATAGAAAAAAEAAAAAEEAEAAAAAAAAAAAAA
AAAAAAEAEAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
ATTTATTSTTTTATTTTSTTAATTTTTTTTATATTTAAT
QQQQQQQQQKQQQEQQEQQQQEEEEEQQQQQQQQQQQQQ
RRRRRKKRRRRRKRKRRRRKRRRRRRRKRKKRRRRRRRR
YYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYY
YYYYLHLYYYSYYYYCYYYYCYYYYYYYYYYYYYYYLYY
VVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVV
EEEEEEEEEEDEDEEDEEEEEEEEEEEDEEDEEEEEEEE
RRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRR
PPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPP
IIIIIIIIIIIIIIILIIIIIIIIIIIIIIIIIIIIIII
FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF
SSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSS
HHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH
LPPPPPPPPPPPLLSLLLPPPLLLLLPLPSLPPPPPPPP
VVVVVVVVVVVVAVVAAVVVVVAAVAVAVVAIVVVVVVV
LLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLL
QQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQ
EEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE
RRRRKRRRRRRRIRRRRRRRRRRRRRRRRRIRRRRRRRR
LLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLL
HHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH
KKKKKVVKVKKKTKKKKKKKKKKKKKTTKKTQKKKKKKK
KKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKK
DDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDD
KKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKK
IIIIIVVIVVVIIIIIIIIIVIIIIIVIIIIIIIIIIII
SPPPPSTSTSPSPSSPSSPPASSSSSPPPSPSSSSSSSP
DDDDDEDDEDDDDDDDDDDDDDDDDDDDDEDDDEEEDDD
SSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSS
IIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIII
GGGGGGGGGGGGGGGGGGGGGGGGGGAGGGGGGGGGGGG
DDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDD
KKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKK
LLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLL
KKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKK
QQQQRQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQ
AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
FFFFFFFFFFFFFFFFFFFFSFFFFFFFFFFFFFFFFFF
TTTTTTTTTTITTTTTTTTTTTTTTTTTTTTTTTTTTTT
CCCCCCCCCCCCCCCCCCCCYCCCCCCCCCCCCCCCCCC
TTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTT
PPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPP
KKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKK
KKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKK
IIIIIIVIIIIIIIIIIIIIIIIIIIIVIIIIIIIIIII
RRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRR
NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN
IIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIII
IIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIII
YYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYY
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF
LLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLL
PPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPP
IIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIII
TTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTT
KKKKKKKKKKKKKKKKKKKKKKKKKKKKKKEEKKKKKKK
WWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWW
LLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLL
PPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPP
AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
YYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYY
RRRRQKKKKKKKKNNKNKKKKNNNNNKKKNKKRRRRRRK
FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF
KKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKK
EEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE
YYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYY
VVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVV
LLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLL
GGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG
DDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDD
LLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLIIILLL
VVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVV
SSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSS
GGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG
IIIIIIIIIIIIIIIIVIIIIIVVIVIIIIIIIIIIIII
SSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSS
TTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTT
GGGGGGGGGGRGGGGGGGGGGGGGGGGGGGGGGGGGGGG
VVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVV
LLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLL
QQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQ
LLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLL
PPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPP
QQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQ
GGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG
LLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLL
AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF
AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
LLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLL
AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
VVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVV
PPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPP
PPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPP
VVVVVVVVVVVVVVVVVVVVVVVVVVIVVVVVVVVVVVV
FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF
GGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG
LLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLL
YYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYY
SSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSS
SSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSS
FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF
YYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYY
PPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPP
VVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVV
IIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIF
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
YYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYY
CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC
FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF
FFFFFFFLFFLFFFFFFFFFLFFFFFLFFFFFFFFFFFF
GGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG
TTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTT
SSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSS
RRRRRRRRRRRRRRRRRRRRRRRRRRRRRRKKRRRRRRR
HHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH
IIIIIIIIIVIIIIIIIVIIIVIIIIIIIIIIIIIIIII
SSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSS
IIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIII
GGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG
PPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPP
FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF
AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
VVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVV
IIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIII
SSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSS
LLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLL
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
IIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIII
GGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG
GGGGGGGGGGAGGGGGGGGGGGGGGGGGGGGGGGGGGGG
VVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVV
AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
VVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVV
RRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRR
LLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLL
VVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVV
PPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPP
DDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDD
DDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDD
MIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIMIIIIMI
VVVVVVVVVVVVVAVVAVVVVAAAAAVVVVVVVVVVVVV
IIIIIIIIIIIIIVIIPIIIIVPPVPIIIIIIIIIIIII
PPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPP
GGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG
GGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG
VVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVV
NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN
AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
TTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTT
NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN
GGGGGGGGGGGGGGGGSGGGGGSSGSGGGGGGGSSSSGG
TTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTT
EEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE
AAAAAAAAAAAAAFAIFAAAAFFFFFAAAIAAAAAAAAA
RRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRR
DDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDD
AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
LLLLLLLLLLLLLLLLLLLLLLLLLLLLLLMLLLLLLLL
RRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRR
VVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVV
KKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKK
VVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVV
AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
SSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSS
VVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVV
TTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTT
LLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLL
LLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLL
STTTTSSSSSSSAASSASSSSAAAAASSSSTSSTTTSSS
GGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG
IIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIII
IIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIII
QQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQ
FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF
CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC
LLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLL
GGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG
VVVVVVVVVVVVVVVVVVVVVIVVVVVVVVVVVVVVVVV
CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC
RRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRR
FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF
GGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG
FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF
VVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVV
AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
IIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIII
YYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYY
LLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLL
TTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTT
EEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE
PPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPP
LLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLL
VVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVV
RRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRR
GGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG
FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF
TTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTT
TTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTT
AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
VVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVV
HHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH
VVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVV
FFFFFFFFFFFFFFFFAFFFFFAAFAFFFFFFFFFFFFF
TTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTT
SSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSS
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
LLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLL
KKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKK
YYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYY
LLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLL
FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF
GGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG
VVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVV
KKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKK
TTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTT
KKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKK
RRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRR
YYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYY
SSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSS
GGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG
IIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIII
FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF
SSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSS
VVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVV
VVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVV
YYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYY
SSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSS
TTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTT
VVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVV
AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
VVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVV
LLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLL
QQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQ
NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN
VVVVVVVVVVVVVVVVVVVVIVVVVVVVVVVVVVVVVVV
KKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKK
NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN
LLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLL
NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN
VVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVV
CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC
SSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSS
LLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLL
GGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG
VVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVV
GGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG
LLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLL
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
VVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVV
FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF
GGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG
LLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLL
LLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLL
LLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLL
GGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG
GGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG
KKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKK
EEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE
FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF
NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN
EEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE
RRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRR
FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF
KKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKK
EEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE
KKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKK
LLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLL
PPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPP
AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
PPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPP
IIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIII
PPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPP
LLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLL
EEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE
FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF
FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF
AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
VVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVV
VVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVV
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
GGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG
TTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTT
GGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG
IIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIII
SSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSS
AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
GGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG
FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF
NNNNSNNNNNNNSGSSNNNNNGNNGNNSNSSNNSSSSNN
LLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLL
NHHHHHHKHHQKHHHHHQHHKHHHHHKHHHHHHHHHHHH
EEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEDEEEEEEE
SSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSS
YYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYY
NNNNNSSNSNNNNNNNNNNNSNNNNNNNNNNNNNNNNNN
VVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVV
DDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDD
VVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVV
VVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVV
GGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG
TTTTTTTTTTTTSTTTTTTTTTTTTTTTTTTTTTTTTTT
LLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLL
PPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPP
LLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLL
GGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG
LLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLL
LLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLL
PPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPP
PPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPP
AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN
PPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPP
DDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDD
TTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTT
SSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSS
LLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLL
FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF
HHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH
LLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLL
VVVVVVVVVVVVVVVVVVVVVVVVVVVVVVIVVVVVVVV
YYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYY
VVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVV
DDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDD
AAAAAAAAAAAAAAAAAAAAAAAAAAAAAASAAAAAAAA
IIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIII
AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
IIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIII
AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
IIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIII
VVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVV
GGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG
FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF
SSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSS
VVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVV
TTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTT
IIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIII
SSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSS
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
KKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKK
TTTTSTTTTTTTTTTTSTTTTTSSTSTTTTTTTTTTTTT
LLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLL
AAAAGAAAAAAAGAAAAAAAAAAAAAAAAAGAAAAAAAA
NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN
KKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKK
HHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH
GGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG
YYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYY
QQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQ
VVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVV
DDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDD
GGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG
NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN
QQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQ
EEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE
LLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLL
IIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIII
AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
LLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLL
GGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG
LLLLLIILILLLLLLLLLLLLLLLLLLLLLLLLLLLLLL
CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC
NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN
SSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSS
ITTTIIIIIIIIITIITIIIITTTTTIIIIVIITTTTII
GGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG
SSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSS
LLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLL
FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF
QQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQ
TTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTT
FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF
SAAAASSSSSSSAASSASSSSAAAAASSSSASSAAAASS
IIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIII
SSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSS
CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC
SSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSS
LLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLL
SSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSS
RRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRR
SSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSS
LLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLL
VVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVV
QQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQ
EEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE
GGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG
TTTTTTTTTTTTTITTTTTTTTTTTTTTTTTTTTTTTTT
GGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG
GGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG
KKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKK
TTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTT
QQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQ
LLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLL
AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
GGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG
CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC
LLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLL
AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
SSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSS
LLLLILLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLL
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
IIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIII
LLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLL
LLLLLLLLLLLLMMMMMLMMLMMMMMLMMMMMLLLLLLM
VVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVV
IIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIII
LLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLL
AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
TTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTT
GGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG
FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF
LLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLL
FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF
EEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE
SSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSS
LLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLL
PPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPP
QQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQ
AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
VVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVV
LLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLL
SSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSS
AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
IIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIII
VVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVV
IIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIII
VVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVV
NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN
LLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLL
KKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKK
GGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
QQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQ
FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF
SSSSSSSSSSSSSSSSSSSSASSSSSSSSSSSSSSSSSS
DDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDD
LLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLL
PPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPP
FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF
FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF
WWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWW
RRRRRRRRRRRRRRRRRRRRRKRRRRRRRRRRRRRRRRR
TTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTT
SSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSS
KKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKK
IIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIII
EEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE
LLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLL
TTTTTTTTTTTTTVTTTTTTTTTTTTTTTTTTTTTTTTT
IIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIII
WWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWW
LLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLL
TTTTTTTTTTTTTSTTSTTTTSSSSSTTTTTATTTTTTT
TTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTT
FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF
VVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVV
SSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSS
SSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSS
LLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLL
FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF
LLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLL
GGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG
LLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLL
DDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDD
YYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYY
GGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG
LLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLL
IIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIII
TTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTT
AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
VVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVV
IIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIII
IIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIII
AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
LLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLL
LMMMMLLLLLLLMMMMMLLMLMMMMMLMMMMMLMMMMLM
TTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTT
VVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVV
IIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIII
YYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYY
RRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRR
TTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTT
QQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQ
SSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSS
PPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPP
SSSSSSSSSSSSSTSSTSSSSTTTTTSSSSSSSSSSSSS
YYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYY
KIIIKKTKKKKKKTKKTKKKKTTTTTKKKKKKKIIIKKK
VVVVVVVVVVVVVVVVVVVVVVVVVVVIVVVVVVVVVVV
LLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLL
GGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG
QQQQQQQQQQQQQQQQQQQQQQQQQQKQQQQQQQQQQQQ
LLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLL
PPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPP
DDDDDDDDDDDEDDDDDDDDDDDDDDEDDDDDDDDDDDD
TTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTT
DDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDD
VVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVV
YYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYY
IIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIII
DDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDD
IIIIIIIIIIIIIIIIIIIIIIIKIIIIIIIIIIIIIII
DDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDD
AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
YYYYYYYYYYFYYYYYYYYYYYYYYYYYYYYYYYYYYYY
EEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE
EEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE
VVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVV
KKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKK
EEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE
IVVVIIIIIIIIIIIIIIIIIIIIIIIIIIVIIVVVIII
PPPPPPPPPPPPPPPPPPPSPSPPPPPPPPPPPPPPPPP
GGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG
IIIIIIIIIIIIIIVIIIIIIIIIIIIIIVIIIIIIIII
KKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKK
IIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIII
FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF
QQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQ
IIIIIIIIIIIIIIIVIIIIIIIIIIIIIIIIIIIIIII
NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN
AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
PPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPP
IIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIII
YYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYY
YYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYY
AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN
SSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSS
DDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDD
LLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLL
YYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYY
SSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSS
SNSSSNSNSNSNSNSSNNNNNSNSSNNSNSSSSSSSSSN
AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
LLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLL
KKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKK
RRRRTRRRRRRRRRRRRRRRRRRTRRRRRRRKRRRRRRR
KKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKK
TTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTT
GGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG
VVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVV
NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNDDNNNNNNN
PPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPP
AAAASAAAAAAAASTASAAAAASSSSAAATAAAAAASAA
LFFFFLIVLFHLFFLLFLFLVFFFFFVLLLFLLFFFFLL
IIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIII
MMMMMMMMMMMMMLMMLMMMMLLLLLMMMMLLMLLMMMM
GGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGRGGGGGGG
AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
RRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRR
RRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRIRRRRRRR
KKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKK
AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
MMMMMMMMMMMMMMMMIMMMMMIIMIMMMMLMMMMMMMM
KKKKKRRKRRRKKKKKRKRRRKRKKRRKRKKKKKKKKKR
KKKKKKKEKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKK
YYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYY
AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
KKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKK
EEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE
VVVVVVVVVVVMVGVVGVVVVGGGGGVVVVVVVVVVVVV
GGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG
NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN
AAAAAAAAAAAAAIAAIAAAAIIIIIAAAVAAAAAAAAA
NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN
MMMMMIIVVMMMLIMMIMMMIIIIIIMMMMIVMMMMMMM
AAAAVAAAAAAAAAVAAAAAAAAAAAAAAVAAAAAAAAA
NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN
AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
TTTTTATTTTTATTTTVTAATTIVTVTTATTITTTTTTA
IVVVVVVVVVVVIDVIDAVVVDDDDDVIVVMVIVVVVIV
VVVVVVVVVVVVIVVVVVIVAVVVVVVIIVIVVVVVVIV
KKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKK
VVVVVVVVVVVVAAAAAVAAVAAAAVAAAAAAVVVVVVA
----------T---------------------------T
---------------------------------------
DDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDD
AAAAAGAAAAAAGAAGAAAAAAAAAAAGAAGAAAAAAAA
EEEEEEEEEEAEEEEEEEEEEEEEEEEQEEEEEEEEEEE
VAVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVV
DDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDD
GGGGGGGGGGGGGAGGAGGGGAAAAAGGGGAGGAAGGGG
EEEEEEEEEEEEEEGEEEEEEEEEEEEEDGEEEEEEEEE
DDDDDNNDNDDDDDDDDDDDNDDDDDDDDDDDDDDDDDD
GGGGGAAAAGGGAGGAGAGGTGGGGGAAGGAGGGGGGGG
TTTTTTTTTTTTTTTTTTTTPTTTTTTTTTTTTTTTTTT
KKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKK
PPPPTPPPPPPPTPPPPPPPPPPPAPPPPPTPPPPPPPP
EEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE
EEEEEEEQEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE
EEEEEEEEEDEGEEEDEEEEEEEEEEEEEEEEEEEEEEE
EEEEEDDDDEEEDEDDED--EGEEEEDD-EEDEEEEEE-
NNNNNDDDDNDDDDDEED--EDEEDEGD-DDDNDDDNN-
EEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE
IIIIIVVVVIIIIVIIIVVVVVIIVIVVVIIIIIIIIIV
KKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKK
YYYYYYFYFYFYFYFFYYYYYYYYYYYFYFFFYYYYYYY
PPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPAP
PPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPP
VVIIIIIIIIVIVVIIVIIIIVVIVVIIIIIIVIIIVVI
VVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVV
ITTTTIIIIITIIITIIIIIVIITTIIIITITITTTTII
KKKKKKKKKKKKKKKKKKKKRKKKKKKKKKKKKKKKKKK
SSSSSTTSTSSSTSSSSSNNSSSSSSSTNSTSSSSSSSN
TTTTTTTTTTTKTTTTTTTTTTTTTTTTTTTTTTTTTTT
LLLLLFFFFLFFIFFFFFFFFFFLFFFIFFIFLLLLLLF
PPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPP
EEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE
EEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE
LLLLLLLLLLLLLLLLLMLLLLLLLLMLLLLLLLLLLLL
QQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQ
RRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRR
FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF
MMMMMMLMLMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
PPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPP
PPPPPQQPQPPPPPPPPPPPPPPPPPPPPPPPPPPPPPP
GGGGGTGGGGGGGLGGLGGGGLLLLLGGGGTGGGGGVGG
DDDDDEEDEDDDDEDDEDDDDQEEEEDEDEDDDDDDDDD
NNNNNNNTNNNNNNNNNNNNGNNNNNNNNNNNNNNNNNN
VVVVVVIIVIIIVVIVVIIIVIVVVVVVVIVVVVVVVVI
HHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH
TTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTT
IIIIVIVVVIIIVIVVVIVVIVVVVVVVVVVVIIIIVIV
IIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIII
LLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLL
DDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDD
FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF
TTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTT
QQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQ
VVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVV
NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN
FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF
IIIIVIMIVIIIIIIIIIIIIIIIIIIIIIIIIMMMVII
DDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDD
SSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSS
VVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVV
GGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG
VVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVV
KKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKK
TTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTT
LLLLLLLLLLLLLLLLLLLLVLLLLLLLLLLLLLLLLLL
AAAASAAAAAAASQSSAAAAAQAAQAASASSAAAAAAAA
GGGGGVGGGGGVGGGGGGGGGGGGGGGGGGGGGGGGGGG
IIIIIMIIIIIMIIIIIIIIIIIIIIIIIIIIIIIIIII
VVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVV
KKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKK
EEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE
YYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYY
GGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG
DDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDD
VVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVV
GGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG
IIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIII
YYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYY
VVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVV
YYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYY
LLLFFLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLFLL
AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
GGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG
CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC
SSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSS
AAAAAPAAPAAAAAPAAAAAAAAAAAAVAPAAAAAAAAA
QQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQ
VVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVV
VVVVVVVVVVVVVVVVVVVVVIVVIVVVVVVVVVVVVVV
NSSSDNNNNNNDNSNNSNNNNSSSSSNNNNSSNSSSDNN
DDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDD
LLLLFLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLL
STTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTSTTTTST
RRRRRRSQRRQRSRQQRQQQRQRRRRRSQQSRRQQRRRQ
NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN
QQQQQRRRNRRRFRQLQRQQRRQQQQRYQQYLRQQQQRQ
FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF
FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF
EEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE
NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN
PPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPP
AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
LLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLL
LLLLLKKKKLKRKLLKLKRRKLLLLLWKRLKLLLLLLLR
EEEEEEEEEEKEEDEEEEEEEDEEDEEEEEEEEDDDEEE
LLLLLLLLLLLLLLLLLLLLLLMLLLLLLLLLLLLLLLL
LLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLL
FFFFFFFFFFFFFFFFFFFFFFFFYFFFFFFFFFFFFFF
HHHHHHHHHHHHHHHHHYHHYHHHHHHHHHHHHHHHHHH
SSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSS
IIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIII
HHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH
DDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDD
AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
VVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVV
LLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLL
GGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG
SSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSS
QQQQQHQQQQQQQLQQLQHIQLLLLLQLHQQQQQQQQQH
VVVVVVVLVAVVLVVLVLVVVVVVVVLLVVLVVVVVVVV
RRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRR
EEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE
AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
LLLLLMMLMLLMLLLLLLLLMLLLLLLLLLLLLLLLLLL
AAAAAAAAAAAAAEAAEAAAAEEEEEAAAAAAAAAAAAA
EEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE
QQQQQQQQQQQRQKQQKQQQQKKKKKQQQQQQQQQQQQQ
EEEEEEEEEEEDEEEEEEEEEEEEEEEEEEEEEEEEEEE
AAAAAATAAGAAAAAAAAAAAVAAAAAAAAAAAAAAAAA
TTTTTSTSTSSSLALLATSSLAAAAASLSLLSTTTTTTS
AAAAAAVAAAAATAATVAAAAAVAAVATAATAAAAAAAA
AVVVVPLLSVPPPAPPTAPPATTTATPPPPPPAAAAVAP
PPPPPPPPLPPPPTPPTPTTPMTTTTPPTPPPPPPPPPA
PPPPPPPPPPPLPPPPLPTTAPLPPLSPTPPPPPPPPPT
QQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQ
EEEEEDEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE
DDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDD
SSSSSMMSMSSSASSSSSLLMSSSSSLALSAASSSSSSM
EEEGEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE
PPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPP
NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN
AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
TTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTT
PPPPPPPPPPPPQPPPPPPPPPPPPPP-PPPPPPPPPPP
EEEEETTTTE-EPDEEEEEETDEEDEA-EEAEEEEEEEE
AAAAATTTTA-AEVPAVAAATVVVVVTAAPEPAAAAAAA
-----PPPP---A-------P-----PE--A--------
-----EEEE-----------E-----EA-----------
-----AAAA-----------A-----A------------
---------------------------------------
-----K--K-----------------K------------
';

chomp($filename = $ARGV[0]);

$psr = PhymlSequenceRepresentation->new();
$psr->loadSequences($filename);

@transposed_sites = $psr->getTransposedSites();

#@transposed_sites = split(/\n/,$alignment);

@SSLS = ExternalFunctions->getPamlSitewiseSSLS("lnf","lnf_concat",\@transposed_sites);

1;
Introduction of supplementary files attached along with the article entitled
"Identification of anticancer drug target genes using an outside 
competitive dynamics model on cancer signaling networks"
Url address: https://github.com/tinhpd80/Total-support

Tien-Dzung Tran* and Duc-Tinh Pham
*Corresponding: trantd@haui.edu.vn
================================================================

1. Acute.totalsupport.wmv
A video file showed how to preprocess Kegg pathways and to conduct 
an experiment to identify anticancer drug target genes of a cancer 
signaling network. 

================================================================

2. Code.txt
A file contains the main C# source code to compute total-support 
by function Competition_AdhereToLeaderBaseOnPageRank
================================================================

3. Supplementary file 03 input.xlsx
A file contains 17 cancer signaling networks after preprocessed from 
KEGG. Note that these 17 networks can be transformed into 17 
text file (.txt) for usage as the input of section 5 entitled
Total support(sum loyalpoint).exe
================================================================

4. Supplementary file 04 output.xlsx
A file contains 17 analysis results of 17 cancer signaling networks,
which include Hierarchical closeness, Closeness, and Total support
================================================================

5. Total support (sum loyalpoint).exe
    (1) NetCmd.exe (analysis program)
	A program runs analysis experiments of cancer signaling networks. 
        Among 41 existent commands, this study used commands of 29 and 33
	
    (2) Input
	This is the default input directory of the analysis program.
        This folder contains 02 subfolders entitled Networktxt and XMLKEGG.
	 
    (3) XMLKEGG
	A folder contains 17 files.xml corresponding to 17 cancer networks 
        downloaded from KEGG on 6/2019. These files are the input of 
        command of 29 in the analysis program
	
    (4) Networktxt 
	A folder contains 17 files.txt corresponding to 17 cancer signaling 
        networks which were preprocessed by command 29. These files are 
        the input of command 33 in the analysis program 

    (5) Command 29: Parse Kegg xml
	Parse Kegg files in format XML in a folder into format file.txt in 
        another folder.
	
    (6) Command 33: node.analysis 
 	Compute centrality of a network i.e., Hierarchical closeness, Closeness, 
	Total support of a network in a file or several networks in a folder.
	
  ================================================================

6. Total support (sum loyalpoint).Javacode
A project developed the algorithms in Java to calculate Total support for 
a large network
      
================================================================
Hanoi, June 10, 2021
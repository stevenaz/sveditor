`ifndef INCLUDED_SV_SYSTEM_TASKS_SVH
`define INCLUDED_SV_SYSTEM_TASKS_SVH
/****************************************************************************
 * system_tasks.svh
 *
 * 
 ****************************************************************************/

/********************************************************************
 * $finish()
 ********************************************************************/
extern task $finish(int n=1);

/********************************************************************
 * $stop()
 ********************************************************************/
extern task $stop(int n=1);

/********************************************************************
 * $exit()
 ********************************************************************/
extern task $exit();

/********************************************************************
 * $time()
 ********************************************************************/
extern function real $time();

/********************************************************************
 * $stime()
 ********************************************************************/
extern function real $stime();

/********************************************************************
 * $realtime()
 ********************************************************************/
extern function real $realtime();

/********************************************************************
 * $realtime()
 ********************************************************************/
extern task $timeformat(int units_number, int precision_number, 
	string suffix_string, int minimum_field_width);
	
/********************************************************************
 * $rtoi()
 ********************************************************************/
extern function integer $rtoi(real real_val);

/********************************************************************
 * $itor()
 ********************************************************************/
extern function real $itor (integer int_val);

/********************************************************************
 * $realtobits()
 ********************************************************************/
extern function bit[63:0] $realtobits(real real_val);

/********************************************************************
 * $bitstoreal()
 ********************************************************************/
extern function real $bitstoreal (bit bit_val);

/********************************************************************
 * $shortrealtobits()
 ********************************************************************/
extern function bit[31:0] $shortrealtobits (shortreal shortreal_val);

/********************************************************************
 * $bitstoshortreal()
 ********************************************************************/
extern function shortreal $bitstoshortreal (bit bit_val);

/********************************************************************
 * $typename()
 ********************************************************************/
extern function string $typename(string data_type);

/********************************************************************
 * $bits()
 ********************************************************************/
extern function integer $bits(string data_type);

/********************************************************************
 * $isunbounded()
 ********************************************************************/
extern function bit $isunbounded(string expr);

`endif /* INCLUDED_SV_SYSTEM_TASKS_SVH */

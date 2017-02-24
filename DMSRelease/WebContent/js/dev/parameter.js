var Description = new Array();
var Query = new Array();
var QueryIDval = new Array();
var asus = new Array();
var actualQuery = "";
var parameterSplitter = "(par)";
var querysplitted = null;
var reqBaseX = null;
var JSONClusterString;
var Minvalue = 0;
var MaxValue = 0;

/*Time Offset*/
var offset = new Date().getTimezoneOffset() * 60 * 1000;

var MinValueWin = 0;
var MaxValueWin = 0;

var detHeatmap;
var NumQuery = 0;

var myRequest = null;
var JSONmeta = null;
var JSONString = null;
//var result= new Array();
var result = null;
var Datain2win = new Array();
var eleminewinindex = 0;

var colorScheme = null;
var colorSchemeNW = null;
var presetMin = 0;
var presetMax = 160;
var pivots = Array();
var pivotssecWin = Array();

var delimiter = '_';

var grapfshower = 0;
var graph1show = 0;
var graph2show = 0;
var countgraph = 0;
var win=null;
var colour = null;
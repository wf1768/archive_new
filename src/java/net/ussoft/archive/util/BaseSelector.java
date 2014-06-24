package net.ussoft.archive.util;

public class BaseSelector {
  
    /**
     * 等于
     * */
    private static String andEqualTo(String field,String value) {
        return field+" = "+ quoteStr(value);
    }

    private static String andNotEqualTo(String field,String value) {
        return field+" <> "+quoteStr(value);
    }

    private static String andGreaterThan(String field,String value) {
        return field+" > "+ quoteStr(value);
    }

    private static String andGreaterThanOrEqualTo(String field,String value) {
        return field+" >= "+ quoteStr(value);
    }

    private static String andLessThan(String field,String value) {
        return field+" < "+ quoteStr(value);
    }

    private static String andLessThanOrEqualTo(String field,String value) {
        return field+" <= "+ quoteStr(value);
    }

    private static String andLike(String field,String value) {
        return field+" like "+ quoteLikeStr(value);
    }
    
    private static String quoteStr(String str){
        if(str==null)
            return null;
        return "'"+str+"'";
    }
    private static String quoteLikeStr(String str){
        if(str==null)
            return null;
        return "'%"+str+"%'";
    }   

    public static String getSql(int operator,String field,String value){
    	String sql = "";
    	switch(operator){
    		case 1:
    			sql = andEqualTo(field, value);
    			break;
    		case 2:
    			sql = andNotEqualTo(field, value);
    			break;
    		case 3:
    			sql = andGreaterThan(field, value);
    			break;
    		case 4:
    			sql = andGreaterThanOrEqualTo(field, value);
    			break;
    		case 5:
    			sql = andLessThan(field, value);
    			break;
    		case 6:
    			sql = andLessThanOrEqualTo(field, value);
    			break;
    		case 7:
    			sql = andLike(field, value);
    			break;
    	}
    	return sql;
    }
    
    @SuppressWarnings("unchecked")
    public static void main(String[] args){
        
    }
    
    
}
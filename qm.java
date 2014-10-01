/*
 * Quin Maclauskey implementation
*/


import java.util.*;

class Minterm 
{
    int number,bitCounts=0,mask;
    boolean valid=false ,dCare=false;
    ArrayList<Integer> indicesWithDash=new ArrayList<> ();
    ArrayList<Integer> varConsidered=new ArrayList<>();
    char[] variable;
    String varConsideredString=new String(" ");
    Scanner inp=new Scanner(System.in);
    Minterm ()
    {
    
    }
    Minterm(boolean dCare ,int a)
    {
        this.dCare=dCare;
        number=a;
        mask=number;
        bitCounts =Integer.bitCount(mask);
        varConsidered.add(number);
                
    }
    
    void copyTo( Minterm x)
    {
        int temp;
        x.number=this.number;
        x.mask=this.mask;
        x.bitCounts=this.bitCounts;
        x.valid=false;
        x.dCare=this.dCare;
        x.indicesWithDash.clear();
        x.varConsidered.clear();
        x.variable=(this.variable);
        for(Integer I :indicesWithDash)
        {
            temp=I;
            x.indicesWithDash.add(temp);
        }
        for(int i=0;i<this.varConsidered.size();i++)
        {
            temp=this.varConsidered.get(i);
            x.varConsidered.add(temp);
        }
        
    }
    
    void makeVarConsideredString()
    {
        for(Integer i: this.varConsidered)
        {
            this.varConsideredString+=new String((String.valueOf(i)))+", ";
        }
    }
    
    boolean consideresVar(Integer i)
    {
        return this.varConsidered.contains(i);
    }
    
}

class TableQM
{
    
    ArrayList <Minterm> allMinterm=new ArrayList<> (); 
    ArrayList<ArrayList <Minterm> > column=new ArrayList<>();
    ArrayList<Minterm> implicants=new ArrayList<>();
    ArrayList<Minterm> uniqueImplicants=new ArrayList<>();
    int N;
 
    Scanner inp=new Scanner(System.in);
    TableQM()
    {
        System.out.println("Creating the reduction table");
        System.out.println("Enter the minterms one by one");
        Minterm temp;
        int i=0;
        String s=inp.nextLine();
        Scanner extract=new Scanner(s);
        while(extract.hasNextInt())
        {
            
            temp=new Minterm(false,extract.nextInt());
            allMinterm.add(temp);
            i++;
            
        }
        System.out.println("Do you have 'don't care' terms? (y/n)");
        if(inp.nextLine().charAt(0)=='y')
        {
            System.out.println("please enter the dont care terms");
            i=0;
            s=inp.nextLine();
            extract=new Scanner(s);
            
            while(extract.hasNextInt())
            {
            
                
                temp=new Minterm(true,extract.nextInt());
                allMinterm.add(temp);
                i++;
            
            }
        }
        System.out.print("Minterms are ");
      /*  for(int j=0; j<allMinterm.size();j++)
        System.out.println(" "+allMinterm.get(j).number+" dc "+allMinterm.get(j).dCare);
        */
        setN();
        for(Minterm m: allMinterm)
            m.indicesWithDash.add(N);
	System.out.printf("\nN Set %d\n",N);
        pair(allMinterm);
       
    }
    final void pair(ArrayList <Minterm> currentColumn)
    {
	
        int elementsInNextColumn=0;
        ArrayList <Minterm> tempColumn=new ArrayList<> (); 
        Minterm c1=new Minterm();
        Minterm c2=new Minterm();
        int i=0;
        while (i<=this.N)
        {
		
            for( Minterm a : currentColumn)
            {
                if(Integer.bitCount(a.mask)==i)
                {
                    for(Minterm b : currentColumn)
                        if(Integer.bitCount(b.mask)==i+1)
                        {
                           
                            boolean hasDashAtSamePlace=checkDash(a,b);
                            if(hasDashAtSamePlace)
                            {
                            
                                a.copyTo(c1);
                                for(int i2=0;i2<c1.indicesWithDash.size();i2++)
                                   c1.mask=setBit0(c1.indicesWithDash.get(i2),c1.number);
                                
                                b.copyTo(c2);
                                for(int i2=0;i2<c2.indicesWithDash.size();i2++)
                                   c2.mask=setBit0(c2.indicesWithDash.get(i2),c2.number);
                             
                            
                               int d=c1.number^c2.number;
                               if( (d & (-d))==d && d!=0)
                                  {
                                                             
                                    d=getIndexOfHighestBit(d);
                                    c1.indicesWithDash.add((d));
                                    for(Integer I : b.varConsidered)
                                    c1.varConsidered.add(I);
                                 
                                    Minterm temp=new Minterm();
                                    c1.copyTo(temp);
                                    tempColumn.add(temp);
                                   
                                    a.valid=true;
                                    b.valid=true;
                                    
                                    elementsInNextColumn++;
                                }
                            }
                        }   
                        
                }
            }
        i++;
        }
        
        
     
        
        column.add(tempColumn);
        if(elementsInNextColumn>=2)
            pair(column.get(column.size()-1));
        
    }
    
 
 
    final void setN()
    {
        int max=allMinterm.get(0).number;
                
        for( Minterm m : allMinterm)
            if(m.number>max) max=m.number;
       
        if(max==0) N=0;
        else N=(32-Integer.numberOfLeadingZeros(max-1));
                
    }
       
    int getIndexOfHighestBit (int number)
    {
       
        if(number==0) return 0;
        else return (32-Integer.numberOfLeadingZeros(number)-1);
    }
    boolean checkDash( Minterm a ,Minterm b)
    {
        Collections.sort(a.indicesWithDash);
        Collections.sort(b.indicesWithDash);
        boolean c=true;
        for(int i=0;i<a.indicesWithDash.size();i++)
            if(!(a.indicesWithDash.get(i).equals(b.indicesWithDash.get(i))))
                return false;
            
        return true;
                
    }
    
    int setBit0(int index,int number)
    {
        int mask=(1<<index);
        
        return (number& (~mask));  
    }
    
    void printImplicants()
    {
       /* Char[] temp;  */
        for(Minterm m : uniqueImplicants)
            {
               
                if(m.valid==false)
                {
                    String a=new String(m.variable);
                    
                    System.out.printf("%s  %s\n",a,m.varConsideredString);
                       
                }       
                
            }
    }
    void makeImplicants()
    {
        for( ArrayList<Minterm> Column: column)
            for(Minterm m : Column)
            {
                         
                if(m.valid==false)
                {
                    Minterm temp=new Minterm();
                    m.copyTo(temp);
                    implicants.add(temp);
                    
                }       
                
            }
        
        this.sortImplicant();
        this.removeDuplicate();
        this.removeDash();
        this.makeVariables();
        this.makeVarString();
    }
    
    protected void sortImplicant()
    {
       
        for(Minterm m:this.implicants)
        {
            Collections.sort(m.varConsidered, new ImplicantComparator());
            Collections.sort(m.indicesWithDash,new ImplicantComparator());
        }
        
        
    }
    
    void removeDuplicate() 
    {
        /*
            This method wont work as it considers the whole 
            minterm object rather than the dash.
        
        HashSet<Minterm> rmDup=new HashSet<>();
        rmDup.addAll(implicants);
        implicants.clear();
        implicants.addAll(rmDup);
        
        create new arraylist, add elements to it after checking 
        duplicacy
        */
        boolean Unique;
        
        for(Minterm m : implicants)
        {
            Unique=true;
            for (Minterm u : uniqueImplicants)
            {
                if(u.varConsidered.equals(m.varConsidered))  Unique=false;
            }
            if(Unique) uniqueImplicants.add(m);
        }
        implicants.clear();
        
        HashSet<Integer> rmDup=new HashSet<>();
        for(Minterm m: uniqueImplicants)
        {
            rmDup.clear();
            rmDup.addAll(m.indicesWithDash);
            m.indicesWithDash.clear();
            m.indicesWithDash.addAll(rmDup);
        }
    }
    
    protected void makeVariables()
    {
        int index;
        char c;
        for(Minterm m : uniqueImplicants)
        {
            index=N-1;
            c='A';
            m.variable=new char[2*N];
            for(int i=0;(i<2*N && index>=0);i++)
            {
            /*
            get the value form the number and chek if the position has a dash
            then fill accordingly
            
            */
              //  System.out.println(" var"+m.variable[i]);
                if(m.indicesWithDash.contains(index))
                {
                    
                   // m.variable[i]='_';
                    c++;
                    //System.out.println("m"+m.indicesWithDash+" "+index+" "+m.variable[i]);
                    index--;
                }
               
                else if(getBitAt(m.number,index))
                {
                    //System.out.println("m"+m.indicesWithDash+" "+index+" true");
                    m.variable[i]=c;
                    c++;
                    index--;
                }
                else //if(!getBitAt(m.number,index))
                {
                    m.variable[i]=c;
                    m.variable[i+1]='\'' ;
                    i++;
                    c++;
                    index--;
                }
                
            }
        }
    }
    
    boolean getBitAt(int number,int index)
    {
        int mask=(1<<index);
        return ((number & mask)==mask) ;
    }
    
    void removeDash()
    {
        for(Minterm m: uniqueImplicants)
        {
            Integer n=N;
            m.indicesWithDash.remove(n);
        }
    }
    
    void makeVarString()
    {
          for(Minterm m: uniqueImplicants)
        {
            m.makeVarConsideredString();
        }
    }
    
}


class ImplicantComparator implements Comparator<Integer>
{
 @Override
    public int compare(Integer o1, Integer o2)
    {
		return (o1>o2 ? 1 : ( o1.equals(o2) ? 0 : -1));
 
    }
}


class ImplicantMatrix extends TableQM
{
    /* algorithms
    recursively apply following steps to each matrix
    
    1) essential implicants
    2) row dominanace
    3) column dominance
    
    if at the end
        we have a null matrix : unique optimal solution
        we have a non null matrix which is not further reducable
            :branch and bound
    */
    ArrayList<Integer> columnNumbers=new ArrayList<>();
    ArrayList<Minterm> essentialPrimeImplicants=new ArrayList<>();
    ArrayList<Minterm> copyUniqueImplicants=new ArrayList<>();
    
    void makeColumnNumbersAndCopyUniqueImplicants()
    {
        
    
        Integer i;
        for(Minterm m: allMinterm)
        {
            if(m.dCare==false)
            {
                i=new Integer(m.number);
                columnNumbers.add(i);
            }
        }
        for(Minterm m: this.uniqueImplicants)
        {
            Minterm temp=new Minterm();
            m.copyTo(temp);
            copyUniqueImplicants.add(temp);
        }
        System.out.print("coln        ");
        for (Integer I: columnNumbers)
            System.out.print(I+"    ");
        System.out.println();
        for(Minterm m: copyUniqueImplicants)
            System.out.println("row "+new String(m.variable));
        
    }
    
    void reduce()
    {
        boolean recursion=false;
        
        System.out.printf("\ncoln        ");
        for (Integer I: columnNumbers)
            System.out.print(I+"    ");
        System.out.println();
        for(Minterm m: copyUniqueImplicants)
            System.out.println("row "+new String(m.variable));
        
        recursion= essentialImplicant(this.copyUniqueImplicants,
                this.columnNumbers);
        //recursion=(recursion || columnDominance(this.copyUniqueImplicants,
          //      this.columnNumbers));
       // recursion=(recursion || rowDominance(this.copyUniqueImplicants,
         //       this.columnNumbers));
        
        if(recursion ) reduce();
        
        
    }
    
    private boolean essentialImplicant(ArrayList<Minterm> row ,
            ArrayList<Integer> col)
    {
        
        boolean found=false;
        int count;
        Minterm m2=new Minterm();
        Minterm rm=new Minterm();
      
        for(Integer i: col)
        {
            
            count=0;
            for( Minterm m: row)
            {
                if(m.consideresVar(i)==true)
                {
                   count++;
                                      
                }
                if(count==1 && 
                         row.get(row.size()-1).varConsidered.equals(m.varConsidered))
                {
                  //  System.out.println("true for "+i);
                    m2=new Minterm();
                    m.copyTo(m2);
                    rm=m;
                
                    //System.out.println("m2.var"+m2.variable);
                    
                }
            }
            if(count==1)
            {
                
                this.essentialPrimeImplicants.add(m2);
                
                found=true;
               // System.out.println("lala "+new String(m2.variable));
                
            }
        }
        
        Integer temp;
        /*
        removing the already considered column
        */
        for(Minterm m: this.essentialPrimeImplicants)
        {
            for(Integer i:m.varConsidered)
                 col.remove(i);
              
            for(int p=0;p<row.size();p++)
            {
                
                if(row.get(p).variable==m.variable)
                {                    
                    row.remove(p);
                }
            }
        }
        
        
        
        return found;
    }
    
    private boolean columnDominance(ArrayList<Minterm> row ,
            ArrayList<Integer> col)
    {
        /*
        column Ci dominates column cj if ci has all the 1 - entries
        in cj . ci is dominating and cj is dominated.
        
        Dominating rows can be removed
        
        a N astrick column can dominate at max an n-1 astrick colum
        */
        
        int astrick=0;
        boolean dominating,found=false;
        ArrayList<Integer> toDelete=new ArrayList<>();
        for(Integer i: col )
        {
            astrick=noOfAstrick(i);
            dominating=true;
            for(Integer j: col)
            {
                if(noOfAstrick(j)<astrick)
                {
                    //check dominance
                    //if dominating , break and delete row
                    //each element in j is contained in i
                    
                    for(Minterm m: row)
                    {
                        if(m.varConsidered.contains(j))
                            if(!m.varConsidered.contains(i))
                            {
                                dominating=false;
                                break;
                            }
                    }
                }
                
                if(dominating) toDelete.add(i);
            }
        }
    
        found=(toDelete.size()>0);
        for(Integer i:toDelete)
           col.remove(i);
      
        toDelete.clear();
        
        return found;
    }
    
    private boolean rowDominance(ArrayList<Minterm> row ,
            ArrayList<Integer> col)
    {
        /*
        Row Ri dominates Rj if Ri has all the 1 - entries
        in Rj and Ri costs (e.g., number of literals) no
        more than rj .
        
        Dominated rows can be removed
        */
        
        int astrick=0;
        boolean dominating,found=false;
        ArrayList<Minterm> toDelete=new ArrayList<>();
        for(Minterm m: row )
        {
            astrick=noOfAstrick(m);
            dominating=true;
            for(Minterm n : row)
            {
                if(noOfAstrick(n)<astrick)
                {
                    //check dominance
                    //if dominating , break and delete row
                    //each element in j is contained in i
                    
                    for(Integer i: n.varConsidered)
                    {
                        if(!m.varConsidered.contains(i))
                        {
                            dominating=false;
                            break;
                        }
                    }
                }
                
                if(dominating) toDelete.add(n);
            }
        }
    
        found=(toDelete.size()>0);
        for(Minterm m:toDelete)
           row.remove(m);
      
        toDelete.clear();
        
        return true;
    }
    
    
    private int noOfAstrick(int i)
    {
        int count=0;
        
            for(Minterm m: this.copyUniqueImplicants)
                if(m.varConsidered.contains(i) ) count++;
        
        return count;
    }
    
    private int noOfAstrick(Minterm m)
    {
        return m.varConsidered.size();
    }
}





public class qm
{
    public static void main (String args[])
    {
        System.out.println("Debug mode");
        System.out.println("Making table element ");
        ImplicantMatrix debug=new ImplicantMatrix();
        System.out.print("\n\n*******\n\n");
        
        debug.makeImplicants();
        debug.printImplicants();

        debug.makeColumnNumbersAndCopyUniqueImplicants();
        debug.reduce();
       /* for(Minterm m : debug.essentialPrimeImplicants)
          System.out.println("ess "+new String(m.variable));
        for(Minterm m : debug.copyUniqueImplicants)
          System.out.println("row rem "+new String(m.variable));
        for(Integer m : debug.columnNumbers)
          System.out.println("col "+m);
       */
        
    }
}

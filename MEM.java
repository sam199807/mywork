package GuanLiQi;
class Block{
	int start;
	int lenth;
	Block next;
	Block prev;
}

class ABC{
	int size;
	Block head;
}

public class MEM {
	ABC []abc=new ABC[15];
	int free_size;
	Block used_head;
	Block malloc(int size){
		int t=(int)((Math.log(size)/Math.log(2))-2);
		Block b=new Block();
		if(abc[t].head!=null){
			b=abc[t].head;
			abc[t].head=abc[t].head.next;
		}else{
			for(;t<=14;t++){
				if(abc[t].head!=null){
					break;
				}
			}
			if(t==15){
				return null;
			}
			b=abc[t].head;
			abc[t].head=abc[t].head.next;
			for(int a=b.lenth;a/2>=size;a=a/2,b.lenth=b.lenth/2){			//判断借出的块是否可以一分为二，若可以将它的一半放入对应的数组中
				Block b2=new Block();
				b2.lenth=a/2;
				b2.start=b.start+a/2;
				t--;
				if(abc[t].head==null){
					abc[t].head=b2;
				}else{
					Block b3=new Block();
					b3=abc[t].head;
					abc[t].head=b2;
					b2.next=b3;
					b2.prev=null;
					b3.prev=b2;
				}
			}
		}
		if(used_head==null){					//将要用的块放到used_head所处的链表中，默认都放在第一个好了
			used_head=b;
			b.prev=null;
			b.next=null;
		}else{
			Block b4=new Block();
			b4=used_head;
			used_head=b;
			b.next=b4;
			b4.prev=b;
			b.prev=null;
		}
		return b;
	}
	int free(Block block){
		if(block==null){
			return -1;
		}
		if(block.prev==null){						//将要释放的内存在链表上面清空
			used_head=block.next;
		}else{
			if(block.next!=null){
				block.prev.next=block.next;
				block.next.prev=block.prev;
			}else{
				block.prev.next=null;
			}
		}
		free_size=block.lenth;
		int t=(int)((Math.log(free_size)/Math.log(2))-2);		//将释放的块的大小转成它对应的数组的下标，看成log运算
		int j=block.start-free_size;
		int k=block.start+free_size;
		int x=0;
		if(abc[t].head==null){				//若对应的块为空，则直接挂上去
			Block b=new Block();
			b.lenth=block.lenth;
			b.next=null;
			b.start=block.start;
			b.prev=null;
			abc[t].head=b;
			return 0;
		}
		Block b1=new Block();
		b1.start=block.start;
		b1.lenth=block.lenth;
		do{										//利用do..while()循环判断是否为对齐的，同时将对齐的块放到更大的空间
			for(Block b=abc[t].head;b!=null;b=b.next){
				if(b.start==j){					//判断b的start是否与block前一个块的start相等，下一个if相同
					if(b.start/(b.lenth*2)==(int)(b.start/(b.lenth*2))){	//判断是否为对齐的
						b1.lenth=b.lenth*2;
						b1.start=b.start;
						if(b.prev!=null){
							if(b.next!=null){
								b.prev.next=b.next;
								b.next.prev=b.prev;
							}else{
								b.prev.next=null;
							}
						}else{
							abc[t].head=b.next;
						}
						x=1;
						j=b1.start-b1.lenth;
						k=b1.start+b1.lenth;
						break;
					}
				}
				if(b.start==k){
					if(b1.start/(b1.lenth*2)==(int)(b1.start/(b1.lenth*2))){
						b1.lenth=b1.lenth*2;
						if(b.prev!=null){
							if(b.next!=null){
								b.prev.next=b.next;
								b.next.prev=b.prev;
							}else{
								b.prev.next=null;
							}
						}else{
							abc[t].head=b.next;
						}
						x=1;
						j=b1.start-b1.lenth;
						k=b1.start+b1.lenth;
						break;
					}
				}
				x=0;
			}
			t++;
			if((abc[t].head==null)&x==1){				//如果更大的块上的链表是空的话就不需要考虑合并了，直接想办法跳出循环，然后挂上去
				abc[t].head=b1;
				return 0;
			}
		}while(x==1);		//不能合并了就挂上去
		Block b2=abc[t-1].head;
		abc[t-1].head=b1;
		b1.next=b2;
		b2.prev=b1;
		b1.prev=null;
		return 0;
	}
	
	public static void main(String []args){
		MEM mem=new MEM();
		int s=4;
		for(int i=0;i<mem.abc.length;i++){		//初始化ABC
			ABC abc=new ABC();
			abc.size=s;
			mem.abc[i]=abc;
			s=s*2;
		}
		Block b=new Block();					//定义这个块65536
		b.lenth=65536;
		b.start=0;
		b.next=null;
		b.prev=null;
		mem.abc[14].head=b;
		int c=0;
		while(c!=5){							//利用while循环随机调用malloc和free方法
			int x=(int)(Math.random()*15)+2;
			int t=1;
			for(int m=1;m<=x;m++){
				t=t*2;
			}
			mem.malloc(t);
			c++;
		}
		c=0;
		while(c!=5){							//利用while循环随机调用malloc和free方法
			int q;
			Block b1=mem.used_head;
			Block b2=mem.used_head;
			for(q=0;b1!=null;b1=b1.next){
				q++;
			}
			int w=(int)(Math.random()*(q))+1;
			for(int e=1;e<=(w-1);e++){
				b2=b2.next;
			}
			mem.free(b2);
			c++;
		}
		for(int i=0;i<=14;i++){					//输出数组里所有块的start
			for(Block b1=mem.abc[i].head;b1!=null;b1=b1.next){
				System.out.print(b1.lenth+" ");
			}
			System.out.println();
		}
	}
}

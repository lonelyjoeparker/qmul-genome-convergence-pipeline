# load data
bench = read.table("~/Documents/all_work/programming/java/eclipse/qmul-genome-convergence-pipeline/benchmarks/results/results-benchmarking.tdf",header=T,sep="\t")

# calculate medians etc the easy way via boxplot
model=boxplot(time~alignments*machine*taxa, data=bench)

#get the useful bits for plotting
medians=data.frame(name=model$names,N=as.numeric(model$n),CI_5=as.numeric(model$conf[1,]),median_t=as.numeric(model$stats[3,]),CI_95=as.numeric(model$conf[2,]),alignments=rep(c(10,50,100,500,1000,2000,3000),6),taxa=c(rep("10",14),rep("22",14),rep("4",14)),machine=rep(c(rep("linux",7),rep("mac",7)),3))

# plot the data

## multiplot
quartz()
par(mfrow=c(2,1))

## linux
plot(time~alignments,
     data=bench,
     main="Performance",
     sub ="Ubuntu 16.04LTS // HP PowerEdge // 3.5GHz Xeon // 64Gb RAM",
     xlab="Size (# alignments)",
     ylab="Time (s, wall-clock)",
     axes=F,
     size=5,
     col="white")
axis(side=1)
axis(side=2)
legend(0,32.5,lty=3,pch=16,bty="n","4 taxa")
legend(0,27.5,lty=3,pch=17,bty="n","22 taxa",col="blue")

# linux, 4spp
points(time~alignments,data=bench[bench$taxa=="4spp"&bench$machine=="linux",],pch=3,cex=1.5,col="grey")
points(median_t~alignments,data=medians[medians$taxa=="4"&medians$machine=="linux",],pch=16,cex=1.5)
lines(median_t~alignments,data=medians[medians$taxa=="4"&medians$machine=="linux",],lwd=3,lty=3)
# linux, 10spp
#points(time~alignments,data=bench[bench$taxa=="10spp"&bench$machine=="linux",],pch=4,size=5,col="pink")
#lines(median_t~alignments,data=medians[medians$taxa=="10"&medians$machine=="linux",],lwd=3,col="red",lty=3)
#points(median_t~alignments,data=medians[medians$taxa=="10"&medians$machine=="linux",],size=5,pch=17,col="red")
# linux, 22spp
points(time~alignments,data=bench[bench$taxa=="22spp"&bench$machine=="linux",],pch=4,cex=1.5,col="light blue")
points(median_t~alignments,data=medians[medians$taxa=="22"&medians$machine=="linux",],pch=17,cex=1.5,col="blue")
lines(median_t~alignments,data=medians[medians$taxa=="22"&medians$machine=="linux",],lwd=3,col="blue",lty=3)

## mac
plot(time~alignments,
     data=bench,
     #main="Performance",
     sub ="OSX 10.9.5 // Apple MacBook Pro // 2.2GHz Core i7 // 8Gb RAM",
     xlab="Size (# alignments)",
     ylab="Time (s, wall-clock)",
     axes=F,
     col="white")
axis(side=1)
axis(side=2)
# mac, 4spp
points(time~alignments,data=bench[bench$taxa=="4spp"&bench$machine=="mac",],pch=3,cex=1.5,col="grey")
points(median_t~alignments,data=medians[medians$taxa=="4"&medians$machine=="mac",],pch=16,cex=1.5)
lines(median_t~alignments,data=medians[medians$taxa=="4"&medians$machine=="mac",],lwd=3,lty=3)
# mac, 10spp
#points(time~alignments,data=bench[bench$taxa=="10spp"&bench$machine=="mac",],pch=4,col="pink")
#points(median_t~alignments,data=medians[medians$taxa=="10"&medians$machine=="mac",],col="red")
#lines(median_t~alignments,data=medians[medians$taxa=="10"&medians$machine=="mac",],col="red",lty=3)
# mac, 22spp
points(time~alignments,data=bench[bench$taxa=="22spp"&bench$machine=="mac",],pch=4,cex=1.5,col="light blue")
points(median_t~alignments,data=medians[medians$taxa=="22"&medians$machine=="mac",],pch=17,cex=1.5,col="blue")
lines(median_t~alignments,data=medians[medians$taxa=="22"&medians$machine=="mac",],lwd=3,col="blue",lty=3)

# with ggplot2
library(ggplot2)


par(mfrow=c(2,1))
#linux
ggplot(
  bench[bench$machine=="linux"&bench$taxa!="10spp",],
    aes(alignments,time, colour=taxa)
) + 
  labs(
    title="Performance: Ubuntu 16.04LTS // HP PowerEdge // 3.5GHz Xeon // 64Gb RAM",
    x="Size (# alignments)",
    y="Time (s, wall-clock)"
  ) +
  geom_smooth(data= subset(bench, machine=="linux"&taxa!="10spp"),method="loess",span=1.5,level=0.999) + 
  geom_jitter(positon=position_jitter(width=15), size=3)

#mac
ggplot(
  bench[bench$machine=="mac",],
  aes(alignments,time,colour=taxa)
) + 
  labs(
    title="Performance: OSX 10.9.5 // Apple MacBook Pro // 2.2GHz Core i7 // 8Gb RAM",
    x="Size (# alignments)",
    y="Time (s, wall-clock)"
  ) +
  geom_smooth(data= subset(bench, machine=="mac"),method="loess",span=1.5,level=0.999) + 
  geom_jitter(positon=position_jitter(width=10),size=3)


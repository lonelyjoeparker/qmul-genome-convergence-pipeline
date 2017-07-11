
# coding: utf-8

# In[2]:

import os, sys, subprocess, time
from shutil import copyfile

# dir = '/mnt/1C58FFD758FFAE20/nature_12511/cds_alns_20120815/'
#
# ## try and copy some files by selecting randomly
#
# for i in range(3):
#     empty_list_dicts = {}
#     for file in os.listdir(dir):
#         #print file
#         empty_list_dicts[os.urandom(8)] = file
#
#     print "\n"
#     selection = 0
#     for k, v in sorted(empty_list_dicts.items()):
#         if selection < 5:
#             print k, "\t\t", v
#             fullpath = dir + v
#             copyfile(fullpath,"./benchmark_inputs/"+v)
#             selection+=1
#     print "\n\n"


# In[116]:

#import time

## time a system process

#subprocess.call(["ls","-laht"])

#print subprocess.check_output("ls -laht", shell=True)
timing_string = 'java -jar ../trunk/builds-snapshots/CONTEXT-v0.8.4prerelease.jar /mnt/1C58FFD758FFAE20/nature_12511/manually_curated_2 >/dev/null 2>/dev/null'
timing_string = 'java -jar ../trunk/builds-snapshots/CONTEXT-v0.8.4prerelease.jar /mnt/1C58FFD758FFAE20/nature_12511/cds_alns_20120815  >/dev/null 2>/dev/null'

then = time.time()
#print subprocess.check_output(timing_string, shell=True)
now = time.time()
#print (now - then)

reps = {}
index = 0
# for i in range(3):
#     then = time.time()
#     print subprocess.check_output(timing_string, shell=True)
#     now = time.time()
#     reps[index] = (now - then)
#     index += 1


print "\t".join(str(x) for x in reps.values())

# sampled_files = os.listdir(sampling_dir)
# for file in sampled_files:
#     if file.endswith(".fas"):
#         os.remove(os.path.join(sampling_dir,file))

# In[129]:
#
nature_dir = '/mnt/1C58FFD758FFAE20/nature_12511/cds_alns_20120815/'
timing_string_replicates = 'java -jar ../trunk/builds-snapshots/CONTEXT-v0.8.4prerelease.jar ./benchmark_inputs/  >/dev/null 2>/dev/null'
source_contents = os.listdir(nature_dir)
replicates = 5
sampling_dir = "./benchmark_inputs/"
#
#for sample in [10,50,100,500,1000]:
#     print 'sampling' , sample
#
#     reps = {}
#     index = 0
#     for r in range(replicates):
#         empty_list_dicts = {}
#         #print 'replicate',r
#
#         for file in source_contents:
#             #print file
#             empty_list_dicts[os.urandom(8)] = file
#
#
#         selection = 0
#         for k, v in sorted(empty_list_dicts.items()):
#             if selection < sample:
#                 #print k, "\t\t", v
#                 fullpath = os.path.join(nature_dir,v)
#                 destpath = os.path.join(sampling_dir,v)
#                 copyfile(fullpath,destpath)
#                 selection+=1
#
#         then = time.time()
#         print subprocess.check_output(timing_string_replicates, shell=True)
#         now = time.time()
#         print sample, "22sp\t," , (now - then)
#         index += 1
#         #print "\n"
#
# #    print "\t".join(str(x) for x in reps.values())
# #    print "\n--\n"
#     sampled_files = os.listdir(sampling_dir)
#     for file in sampled_files:
#         if file.endswith(".fas"):
#             os.remove(os.path.join(sampling_dir,file))


#
# # In[132]:
#
#repeat for the 4spp and 10spp cases. they have more loci so we can push the sample sizes a bit B)

# mao_4_spp  = '/mnt/1C58FFD758FFAE20/mao_for_joe_4_spp/'
# mao_10_spp = '/mnt/1C58FFD758FFAE20/mao_for_joe_10_spp-genes3655/'
#
# timing_string_replicates = 'java -jar ../trunk/builds-snapshots/CONTEXT-v0.8.4prerelease.jar ./benchmark_inputs/  >/dev/null 2>/dev/null'
#
# source_contents = os.listdir(mao_4_spp)
# replicates = 5
# sampling_dir = "./benchmark_inputs/"
#
# for sample in [10,50,100,500,1000,2000,3000]:
#     print 'sampling 4spp' , sample
#
#     reps = {}
#     index = 0
#     for r in range(replicates):
#         empty_list_dicts = {}
#         #print 'replicate',r
#
#         for file in source_contents:
#             #print file
#             empty_list_dicts[os.urandom(8)] = file
#
#
#         selection = 0
#         for k, v in sorted(empty_list_dicts.items()):
#             if selection < sample:
#                 #print k, "\t\t", v
#                 fullpath = os.path.join(mao_4_spp,v)
#                 destpath = os.path.join(sampling_dir,v)
#                 copyfile(fullpath,destpath)
#                 selection+=1
#
#         then = time.time()
#         print subprocess.check_output(timing_string_replicates, shell=True)
#         now = time.time()
#         print sample, " 4spp\t," , (now - then)
#         index += 1
#
#     # delete files before starting a new replicate
#     sampled_files = os.listdir(sampling_dir)
#     for file in sampled_files:
#         if file.endswith(".fas"):
#             os.remove(os.path.join(sampling_dir,file))
#

#

# In[4]:

#repeat for the 4spp and 10spp cases. they have more loci so we can push the sample sizes a bit B)

mao_4_spp  = '/mnt/1C58FFD758FFAE20/mao_for_joe_4_spp/'
mao_10_spp = '/mnt/1C58FFD758FFAE20/mao_for_joe_10_spp-genes3655/'

timing_string_replicates = 'java -jar ../trunk/builds-snapshots/CONTEXT-v0.8.4prerelease.jar ./benchmark_inputs/  >/dev/null 2>/dev/null'

source_contents = os.listdir(mao_10_spp)
replicates = 5
sampling_dir = "./benchmark_inputs/"

samples = [10,50,100,500,1000,2000,3000]
samples.reverse()
for sample in samples:
    print 'sampling 10spp' , sample

    reps = {}
    index = 0

    #read in files, random keys to sort them on
    for r in range(replicates):
        empty_list_dicts = {}
        #print 'replicate',r

        for file in source_contents:
            #print file
            empty_list_dicts[os.urandom(8)] = file


        # select (sample) of them
        selection = 0
        for k, v in sorted(empty_list_dicts.items()):
            if selection < sample:
                #print k, "\t\t", v
                fullpath = os.path.join(mao_10_spp,v)
                destpath = os.path.join(sampling_dir,v)
                copyfile(fullpath,destpath)
                selection+=1

        # run the context benchmark on this sample
        then = time.time()
        print subprocess.check_output(timing_string_replicates, shell=True)
        now = time.time()
        print sample, " 10spp\t," , (now - then)
        index += 1

    # delete files before starting a new replicate
    sampled_files = os.listdir(sampling_dir)
    for file in sampled_files:
        if file.endswith(".fas"):
            os.remove(os.path.join(sampling_dir,file))

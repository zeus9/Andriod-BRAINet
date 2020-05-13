import os


base_dir = '/Users/jk/dev/CSE535-BraiNetProject/matlab/all_clean'
out_dir = '/Users/jk/dev/CSE535-BraiNetProject/matlab/with_classes'

def add_classes(parent_folder, files):
  for datafile in files:
    user = parent_folder.split('_').pop()

    out_dir_user = out_dir + '/' + user
    if not os.path.exists(out_dir_user):
        os.makedirs(out_dir_user)

    with open(parent_folder + '/' + datafile) as f_in, open(out_dir_user + '/' + datafile, 'w') as f_out:
      for line in f_in:
        f_out.write(','.join([user] + line.split(',')))
        # import pdb; pdb.set_trace()

def split_data(files):
  half = len(files)/2
  print(f'{half}/{len(files)}')
  for i, datafile in enumerate(files):
    if i < half:
      # train
      pass
    else:
      # test
      pass

#get all folders starting with "freq_clean"
freq_folders = list(filter(lambda x: 'freq_clean' in x, os.listdir(base_dir)))
print('frequency folders: ', freq_folders)

for folder in freq_folders:
  files = os.listdir(base_dir + '/' + folder)
  add_classes(base_dir + '/' + folder, files)

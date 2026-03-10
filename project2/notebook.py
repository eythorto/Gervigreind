import os
import numpy as np

# Data preparation (Task 6.1)
# NB:
# Total files: 1132 in data/out
# Train: 792, Val: 113, Test: 227

# paths to data
SPLITS_AND_FEATURES_DIR = "data\\out\\"
LABEL_DIR = "data\\cmu_us_slt_arctic\\lab\\"

# Read train.txt, val.txt, and test.txt to obtain basenames for each split.
def make_basenames(split_file):
    with open(split_file, "r") as file:
        basenames = [line.strip() for line in file]
    return basenames

# Load files as lists
train_ids = make_basenames(os.path.join(SPLITS_AND_FEATURES_DIR, "train.txt"))
test_ids = make_basenames(os.path.join(SPLITS_AND_FEATURES_DIR, "test.txt"))
val_ids = make_basenames(os.path.join(SPLITS_AND_FEATURES_DIR, "val.txt"))

def get_all_arctic_basenames(label_dir):
    basenames = []

    for filename in os.listdir(label_dir):
        if filename.startswith("arctic") and filename.endswith(".lab"):
            basename = filename.replace(".lab", "")
            basenames.append(basename)

    return sorted(basenames)

# phoneme set and a mapping from phoneme symbols to integer indices
def load_label_file(basename):
    label_path = os.path.join(LABEL_DIR, basename + ".lab")
    segments = []
    with open(label_path, "r") as file:
        next(file) # skip the header of the file, its "#"
        for line in file:
            parts = line.strip().split()

            timestamp = float(parts[0])
            phoneme = parts[2]

            segments.append((timestamp, phoneme))

    return segments

def extract_phoneme_set(basenames):
    phonemes = set()

    for basename in basenames:
        segments = load_label_file(basename)
        for _, ph in segments:
            phonemes.add(ph)

    # We want pau to be on index 0
    
    sorted_list = sorted(list(phonemes))
    sorted_list.remove("pau")
    sorted_list.insert(0, "pau")
    return sorted_list

all_ids = get_all_arctic_basenames(LABEL_DIR)
print("Total files found:", len(all_ids)) #should be 1132!

phoneme_set = extract_phoneme_set(train_ids) # could also be all_ids?
phoneme_to_idx = {p: i for i, p in enumerate(phoneme_set)} 
idx_to_phoneme = {i: p for p, i in phoneme_to_idx.items()}
print("The Phoneme set", phoneme_set) 
print("Phoneme set to ints", phoneme_to_idx) # 40 phonemes mapped to an int (+ pau) ~41 in total
print("Ints to phonemes", idx_to_phoneme)


# Load feature files
def load_feature_matrix(basename):
    """
    (T, D)
    T = number of frames
    D = feature dimension (always 39?)
    """
    feature_path = os.path.join(SPLITS_AND_FEATURES_DIR, basename + ".npy")
    features = np.load(feature_path)
    return features

# Example of feature tuple
features = load_feature_matrix("arctic_a0001")
print("Shape of feature matrix", features.shape)  # -> (334, 39)


# Convert time stamps -> assign each phoneme label a frame
# These timestamps represent when the phoneme becomes active.
# Example: frames between t0 and t1 → phoneme0

def align_frames_to_phonemes(features, segments, phoneme_to_idx, frame_shift=0.01):
    T = features.shape[0]
    labels = np.zeros(T, dtype=int)

    for i in range(len(segments)):

        start_time, phoneme = segments[i]

        if i < len(segments) - 1:
            end_time = segments[i + 1][0]
        else:
            end_time = T * frame_shift

        start_frame = int(start_time / frame_shift)
        end_frame = int(end_time / frame_shift)

        start_frame = max(0, start_frame)
        end_frame = min(T, end_frame)

        labels[start_frame:end_frame] = phoneme_to_idx[phoneme]

    return labels

def process_audio_sample(basename):
    """Combine features and labels"""
    features = load_feature_matrix(basename)
    segments = load_label_file(basename)

    labels = align_frames_to_phonemes(features, segments, phoneme_to_idx)

    assert len(labels) == features.shape[0]

    return features, labels


features, labels = process_audio_sample(train_ids[0])

# equal so data is ready for HMM training! Yibby :D
print("Frames:", features.shape[0])
print("Labels:", len(labels))

# for each audio sample we have
# O1:T = feature vectors (from .npy)
# X1:T = phoneme labels (integers)

def build_dataset(basenames):
    """Apply it to the train.txt set"""
    dataset = []

    for basename in basenames:
        features, labels = process_audio_sample(basename)
        dataset.append((features, labels))

    return dataset

train_data = build_dataset(train_ids)
val_data = build_dataset(val_ids)
test_data = build_dataset(test_ids)



# Supervised parameter estimation (Task 6.2)

# initial distribution
# skip over pau? - since all of them start with pau

initial_dict = {}

for data in train_data:
    num = int(data[1][0])
    if num in initial_dict.keys():
        initial_dict[num] += 1
    else:
        initial_dict[num] = 1


# transition matrix

transition_dict = {}
instances_dict = {}

for data in train_data:
    labels = data[1]
    for i in range(len(labels)-1):
        current_p = labels[i]
        next_p = labels[i+1]
        if current_p in transition_dict.keys():
            instances_dict[current_p] += 1
            if next_p in transition_dict[current_p].keys():
                transition_dict[current_p][next_p] += 1
            else:
                transition_dict[current_p][next_p] = 1
        else:
            instances_dict[current_p] = 1
            transition_dict[current_p] = dict()
            transition_dict[current_p][next_p] = 1

print(instances_dict)




# emission parameters
# calculate the mean vector (avg of all feature vectors)
# and covariance matrix 
emission_parameters = {}

for phoneme_idx in range(len(phoneme_set)):
    # Go through training samples
    features_list = []
    for features, labels in train_data:
        for phoneme in labels:
            mask = (labels == phoneme_idx)

            phoneme_features = features[mask]
            if len(phoneme_features) > 0:
                features_list.append(phoneme_features)
    

    if len(features_list) > 0:
        all_features = np.vstack(features_list)
        
        mean = np.mean(all_features, axis=0)
        
        cov = np.cov(all_features, rowvar=False)
        
        cov += np.eye(cov.shape[0]) * 1e-6
        
        emission_parameters[phoneme_idx] = {
            'mean': mean,
            'cov': cov
        }


        
# import matplotlib.pyplot as plt

# # Visualize Gaussian distribution for a single phoneme (e.g., "pau")
# phoneme_idx = 0  # "pau"
# phoneme_name = idx_to_phoneme[phoneme_idx]

# # Get mean and covariance for this phoneme
# mean = emission_parameters[phoneme_idx]['mean']
# cov = emission_parameters[phoneme_idx]['cov']

# # Visualize using first 2 feature dimensions for simplicity
# mean_2d = mean[:2]
# cov_2d = cov[:2, :2]

# # Create a grid for plotting
# x = np.linspace(mean_2d[0] - 3*np.sqrt(cov_2d[0,0]), mean_2d[0] + 3*np.sqrt(cov_2d[0,0]), 100)
# y = np.linspace(mean_2d[1] - 3*np.sqrt(cov_2d[1,1]), mean_2d[1] + 3*np.sqrt(cov_2d[1,1]), 100)
# X, Y = np.meshgrid(x, y)

# # Manual computation of multivariate Gaussian PDF
# inv_cov = np.linalg.inv(cov_2d)
# det_cov = np.linalg.det(cov_2d)

# Z = np.zeros_like(X)
# for i in range(X.shape[0]):
#     for j in range(X.shape[1]):
#         x_vec = np.array([X[i,j], Y[i,j]]) - mean_2d
#         Z[i,j] = np.exp(-0.5 * x_vec @ inv_cov @ x_vec) / (2 * np.pi * np.sqrt(det_cov))

# # Plot
# fig, ax = plt.subplots(figsize=(10, 8))
# contour = ax.contourf(X, Y, Z, levels=20, cmap='viridis')
# ax.plot(mean_2d[0], mean_2d[1], 'r*', markersize=15, label='Mean')
# ax.set_xlabel('Feature Dimension 0')
# ax.set_ylabel('Feature Dimension 1')
# ax.set_title(f'2D Gaussian Distribution for Phoneme "{phoneme_name}"')
# plt.colorbar(contour, ax=ax, label='Probability Density')
# ax.legend()
# plt.tight_layout()
# plt.savefig(f'gaussian_distribution_{phoneme_name}.png', dpi=150)
# plt.show()

# print(f"Mean vector (first 2 dims): {mean_2d}")
# print(f"Covariance matrix (2x2): \n{cov_2d}")
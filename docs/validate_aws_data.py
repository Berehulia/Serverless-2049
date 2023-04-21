import json

EXPECTED_LABELS = [128, 256, 512, 768, 1024, 2048, 3072, 4096, 8192]

def validate(file_name):
    print('Validating "{}" for data quality issues'.format(file_name))
    with open(file_name) as data_file:
        aws_data = json.load(data_file)
        for scenario in aws_data['scenarios']:
            for scenario_data in scenario['data']:
                all_label_measurements = []
                for measurement in scenario_data['measurements']:
                    all_label_measurements.append(measurement['value'])
                missing_labels = list(set(EXPECTED_LABELS) - set(all_label_measurements))
                missing_labels.sort()
                if missing_labels:
                    raise ValueError(
                        'Expecting values for all possible labels to be present, but some labels are missing for function "{}" in {}\n'
                        'Expected: {}\nMissing: {}'.format(scenario_data['function_name'], file_name, EXPECTED_LABELS, missing_labels, ))
                if all_label_measurements != EXPECTED_LABELS:
                    raise ValueError('Expecting values for all possible labels to be present in order, but labels for '
                                     'function "{}" are not ordered as expected in {}\nExpected: {}\nActual: {}'.format(
                            scenario_data['function_name'], file_name, EXPECTED_LABELS, all_label_measurements))
    print('Data in "{}" successfully validated, no issues found'.format(file_name))


if __name__ == "__main__":
    validate('data_aws.json')

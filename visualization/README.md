# Results Visualization
Example: https://i.imgur.com/ykwy7PH.png

## How to decode Base64 encoded metrics from the AWS Lambda Power Tuning URL 
[The AWS Lambda Power Tuning App](https://github.com/alexcasalboni/aws-lambda-power-tuning) uses 3 arrays to store data:
1. The first one will store Powers (Memory configuration in AWS), `Int16Array` array
2. The second one stores an average execution time in ms, `Float32Array` array
3. The third one stores an average execution cost in US dollars, `Float32Array` array

All of these arrays are encoded to Base64 string based on their type and concatenated using `;` as a delimiter. 
A hash `#` symbol is added as a prefix to distinguish a start of such sequence.
Please refer to the algorithm in the official repository - [utils.js#buildVisualizationURL](https://github.com/alexcasalboni/aws-lambda-power-tuning/blob/2baf19fc14f0bcbef2903abc3e17a0f7f1130a14/lambda/utils.js#L556)

To decode this string back to arrays, we need to compute these steps in the opposite order:
1. Remove leading `#` has symbol
2. Split string by the `;` delimiter
3. Convert the string back to the array of a correct type based on its index using [base64-js](https://www.npmjs.com/package/base64-js) (used to encode/decode arrays to Base64 string)

Here is an example code:
```html
<!doctype html>
<html lang="en">
<head>
    <title>The AWS Lambda Power Tuning URL Decoder</title>
    <!-- base64js should be loaded for our script to work -->
    <script src="base64js.min.js"></script>
</head>
<body>
<div>
    <script>
        function decode(x, cls = Float32Array) {
            return Array.from(new cls(window.base64js.toByteArray(x).buffer));
        }
        
        function parseEncodedArraysByIndex(parts) {
            const powers = decode(parts[0], Int16Array);
            const times = decode(parts[1]);
            const costs = decode(parts[2]);
            console.log(powers.toString(), times.toString(), costs.toString());
            return { powers, times, costs };
        }
        // Example of decoding
        parseEncodedArraysByIndex('AAIAAwAEAAg=;xGebQadUYkE4Qj1Bd3dVQQ==;fWM0NO3vSjRjd1g0SYv8NA=='.split(';'))
        
        // Convenience function to convert decoded values to an expected JSON format used for visualization
        function convertToExpectedJSON(functionName, measurements) {
            const result = {
                'function_name': functionName, 
                'measurements': []
            }
            for (let i = 0; i < measurements.powers.length; i++) {
                result.measurements.push(
                    {
                        'value': measurements.powers[i], 
                        'averageDuration': measurements.times[i], 
                        'averagePrice': measurements.costs[i],
                    }
                    );
            }
            return result;
        }
        // Usage example of combining two functions
        console.log(
            JSON.stringify(
                convertToExpectedJSON('functionName', 
                                      parseEncodedArraysByIndex(
                                          'AAIAAwAEAAg=;xGebQadUYkE4Qj1Bd3dVQQ==;fWM0NO3vSjRjd1g0SYv8NA=='.split(';'))
                )
            )
        );
    </script>
</div>
</body>
</html>
```



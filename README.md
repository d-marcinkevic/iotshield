# IoT Shield Challenge
Lets build a tiny security app **IoT Shield** to protect IoT devices based on some allowed behavioral patterns.

We have a fancy AI powered system that provides profile data for devices. Based on this information we can detect devices that are behaving suspiciously (were hacked?!) and stop them from accessing sensitive content.

#### Input of the application (input.json)
Stream of input events is provided as lines in the file, where: 
* `profile_create` and `profile_update` events cover profile lifecycle,
* `request` events represent requests that we're observing for various devices.
```
{"type": "profile_create", "model_name": "iPhone", "default_policy": "block", "whitelist": ["facebook.com"], "blacklist": [], "timestamp": 1562827784}
{"type": "request", "request_id": "66d03a6947c048009f0b34260f35f3bd", "model_name": "iPhone", "device_id": "7fcbe1dff23947e5b3db8a20c1a2f8c0", "url": "facebook.com", "timestamp": 1562827794}
{"type": "request", "request_id": "4cbe5e573971474b8de99f9af24b8949", "model_name": "iPhone", "device_id": "7fcbe1dff23947e5b3db8a20c1a2f8c0", "url": "bad_site_2.com", "timestamp": 1562827884}
{"type": "profile_update", "model_name": "iPhone", "whitelist": ["other_site.com"], "timestamp": 1562827984}
{"type": "request", "request_id": "fe5328eead7e417ab94413afbae907fd", "model_name": "iPhone", "device_id": "7fcbe1dff23947e5b3db8a20c1a2f8c0", "url": "facebook.com", "timestamp": 1562828784}
{"type": "profile_update", "model_name": "iPhone", "whitelist": ["facebook.com"], "timestamp": 1562828785}
{"type": "request", "request_id": "288e1ec951384e6cae2b0de7520afef7", "model_name": "iPhone", "device_id": "7fcbe1dff23947e5b3db8a20c1a2f8c0", "url": "bad_site.com", "timestamp": 1563827784}
```

#### Rules
Behavior of  **IoT Shield** app can be described by several simple rules:

##### Rule 1
Newly defined profile consists of `default_policy` and list of exceptions (`whitelist` for `block` and `blacklist` for `allow`). This information defines correct behaviour for devices of specific model. 

##### Rule 2
Profile updates (`profile_update`) override `whitelist` and `blacklist` values.

##### Rule 3
The app must output the response to each request (`url`). Response value should be based on current state of profile.

##### Rule 4
If a device has a `default_policy` of `block` and sends invalid request(s) it should be moved to quarantine by sending a `quarantine` action.

#### Output of the application (output.json)
- Responses (allow/block/quarantine actions for each request)
```
{"request_id": "66d03a6947c048009f0b34260f35f3bd", "action": "allow"}
{"request_id": "4cbe5e573971474b8de99f9af24b8949", "action": "block"}
{"device_id": "7fcbe1dff23947e5b3db8a20c1a2f8c0", "action": "quarantine"}
```
- Statistics:
  - How many devices were protected by our solution?
  - How many devices are suspected to be hacked?
  - How many blocks were missed due to delayed profile updates?
  - How many blocks were issued incorrectly due to delayed profile updates?

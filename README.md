# JDoc

## Architecture

### Message

```
| 1 byte       | 8 bytes       | 4 bytes        | ...     |
|--------------|---------------|----------------|---------|
| Message type | Request token | Content length | Content |
```
